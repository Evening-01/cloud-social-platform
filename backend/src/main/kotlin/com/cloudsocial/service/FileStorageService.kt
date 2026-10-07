package com.cloudsocial.service

import com.cloudsocial.common.ErrorCode
import com.cloudsocial.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.ImageIO

/**
 * 文件存储服务（本地存储，预留 OSS 扩展点）
 */
@Service
class FileStorageService(
    @Value("\${upload.local-storage:./uploads}") private val storageDir: String,
    @Value("\${upload.image-max-size:10485760}") private val imageMaxSize: Long,
    @Value("\${upload.video-max-size:524288000}") private val videoMaxSize: Long
) {

    private val log = LoggerFactory.getLogger(FileStorageService::class.java)

    private val imageTypes = setOf("jpg", "jpeg", "png", "webp", "gif")
    private val videoTypes = setOf("mp4", "webm", "mov")

    /**
     * 保存上传的媒体文件
     * @return 保存后的相对路径
     */
    fun saveMediaFile(file: MultipartFile): StoredFile {
        val originalName = file.originalFilename ?: "unknown"
        val ext = originalName.substringAfterLast('.', "").lowercase()

        // 类型校验
        val mediaType = when {
            ext in imageTypes -> 1
            ext in videoTypes -> 2
            else -> throw BusinessException(ErrorCode.FILE_TYPE_INVALID, "不支持的文件类型: .$ext")
        }

        // 大小校验
        val maxSize = if (mediaType == 1) imageMaxSize else videoMaxSize
        if (file.size > maxSize) {
            throw BusinessException(ErrorCode.FILE_SIZE_EXCEEDED)
        }

        // 保存文件
        val subDir = if (mediaType == 1) "images" else "videos"
        val newName = "${UUID.randomUUID()}.$ext"
        val relativePath = "$subDir/$newName"
        val target = resolvePath(relativePath)
        Files.createDirectories(target.parent)
        file.transferTo(target.toFile())

        // 视频提取缩略图（FFmpeg 可用时）
        var coverPath: String? = null
        var width: Int? = null
        var height: Int? = null
        var durationSec: Int? = null
        if (mediaType == 2) {
            val cover = extractVideoCover(target)
            coverPath = cover
            val (w, h, d) = probeVideo(target)
            width = w; height = h; durationSec = d
        } else {
            // 图片探测宽高（只读头部，不解码）
            val (w, h) = probeImage(target)
            width = w; height = h
        }

        log.info("保存媒体文件: {} (type={}, size={})", relativePath, mediaType, file.size)
        return StoredFile(
            mediaType = mediaType,
            relativePath = relativePath,
            coverPath = coverPath,
            width = width,
            height = height,
            durationSec = durationSec,
            fileSize = file.size
        )
    }

    /** 提取视频封面（FFmpeg 抽取第 1 秒帧） */
    private fun extractVideoCover(videoPath: Path): String? = try {
        val coverName = videoPath.fileName.toString().substringBeforeLast('.') + ".jpg"
        val coverRelative = "covers/$coverName"
        val coverTarget = resolvePath(coverRelative)
        Files.createDirectories(coverTarget.parent)
        val cmd = listOf(
            "ffmpeg", "-y", "-i", videoPath.toString(),
            "-ss", "00:00:01", "-vframes", "1", "-q:v", "2",
            coverTarget.toString()
        )
        val process = ProcessBuilder(cmd).redirectErrorStream(true).start()
        val exit = process.waitFor()
        if (exit == 0 && Files.exists(coverTarget)) coverRelative else null
    } catch (e: Exception) {
        log.warn("视频封面提取失败（可能未装 FFmpeg）: {}", e.message)
        null
    }

    /** 探测视频宽高和时长 */
    private fun probeVideo(videoPath: Path): Triple<Int?, Int?, Int?> = try {
        val cmd = listOf(
            "ffprobe", "-v", "error", "-select_streams", "v:0",
            "-show_entries", "stream=width,height:format=duration",
            "-of", "default=noprint_wrappers=1", videoPath.toString()
        )
        val process = ProcessBuilder(cmd).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        var w: Int? = null; var h: Int? = null; var d: Int? = null
        output.lines().forEach { line ->
            when {
                line.startsWith("width=") -> w = line.substringAfter('=').toIntOrNull()
                line.startsWith("height=") -> h = line.substringAfter('=').toIntOrNull()
                line.startsWith("duration=") -> d = line.substringAfter('=').toDoubleOrNull()?.toInt()
            }
        }
        Triple(w, h, d)
    } catch (e: Exception) {
        Triple(null, null, null)
    }

    /** 探测图片宽高（只读头部，不解码） */
    private fun probeImage(imagePath: Path): Pair<Int?, Int?> = try {
        ImageIO.createImageInputStream(imagePath.toFile()).use { iis ->
            val readers = ImageIO.getImageReaders(iis)
            if (readers.hasNext()) {
                val reader = readers.next()
                try {
                    reader.setInput(iis)
                    Pair(reader.getWidth(0), reader.getHeight(0))
                } finally {
                    reader.dispose()
                }
            } else {
                Pair(null, null)
            }
        }
    } catch (e: Exception) {
        log.warn("图片宽高探测失败: {}", e.message)
        Pair(null, null)
    }

    private fun resolvePath(relativePath: String): Path =
        Paths.get(storageDir).resolve(relativePath).normalize().toAbsolutePath()
}

/**
 * 存储结果
 */
data class StoredFile(
    val mediaType: Int,
    val relativePath: String,
    val coverPath: String?,
    val width: Int?,
    val height: Int?,
    val durationSec: Int?,
    val fileSize: Long
)
