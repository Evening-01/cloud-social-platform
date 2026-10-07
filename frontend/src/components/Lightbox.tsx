import { useEffect, useState } from "react"
import { ChevronLeft, ChevronRight, X, ZoomIn, ZoomOut, Download } from "lucide-react"
import type { MediaFile } from "@/lib/api"

/** 图片预览器：缩放 + 左右切换 + 下载 */
export function Lightbox({
  media,
  index,
  onClose,
  onNavigate,
}: {
  media: MediaFile[]
  index: number
  onClose: () => void
  onNavigate: (i: number) => void
}) {
  const [scale, setScale] = useState(1)
  const current = media[index]

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose()
      if (e.key === "ArrowLeft" && index > 0) onNavigate(index - 1)
      if (e.key === "ArrowRight" && index < media.length - 1) onNavigate(index + 1)
    }
    window.addEventListener("keydown", onKey)
    document.body.style.overflow = "hidden"
    return () => {
      window.removeEventListener("keydown", onKey)
      document.body.style.overflow = ""
    }
  }, [index, media.length, onClose, onNavigate])

  useEffect(() => setScale(1), [index])

  if (!current) return null

  const isVideo = current.mediaType === 2

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/90" onClick={onClose}>
      {/* 关闭 */}
      <button className="absolute top-4 right-4 z-10 rounded-full bg-white/10 p-2 text-white hover:bg-white/20" onClick={onClose}>
        <X className="h-6 w-6" />
      </button>

      {/* 缩放按钮 */}
      {!isVideo && (
        <div className="absolute top-4 left-4 z-10 flex gap-2">
          <button className="rounded-full bg-white/10 p-2 text-white hover:bg-white/20" onClick={(e) => { e.stopPropagation(); setScale((s) => Math.min(s + 0.5, 4)) }}>
            <ZoomIn className="h-5 w-5" />
          </button>
          <button className="rounded-full bg-white/10 p-2 text-white hover:bg-white/20" onClick={(e) => { e.stopPropagation(); setScale((s) => Math.max(s - 0.5, 0.5)) }}>
            <ZoomOut className="h-5 w-5" />
          </button>
        </div>
      )}

      {/* 下载 */}
      <a
        className="absolute bottom-4 left-4 z-10 rounded-full bg-white/10 p-2 text-white hover:bg-white/20"
        href={current.url}
        download
        onClick={(e) => e.stopPropagation()}
      >
        <Download className="h-5 w-5" />
      </a>

      {/* 左右切换 */}
      {index > 0 && (
        <button className="absolute left-2 z-10 rounded-full bg-white/10 p-2 text-white hover:bg-white/20" onClick={(e) => { e.stopPropagation(); onNavigate(index - 1) }}>
          <ChevronLeft className="h-7 w-7" />
        </button>
      )}
      {index < media.length - 1 && (
        <button className="absolute right-2 z-10 rounded-full bg-white/10 p-2 text-white hover:bg-white/20" onClick={(e) => { e.stopPropagation(); onNavigate(index + 1) }}>
          <ChevronRight className="h-7 w-7" />
        </button>
      )}

      {/* 内容 */}
      <div className="max-h-[90vh] max-w-[90vw]" onClick={(e) => e.stopPropagation()}>
        {isVideo ? (
          <video src={current.url} controls autoPlay className="max-h-[85vh] max-w-[90vw] rounded-lg" />
        ) : (
          <img
            src={current.url}
            className="max-h-[85vh] max-w-[90vw] rounded-lg object-contain transition-transform"
            style={{ transform: `scale(${scale})` }}
          />
        )}
      </div>

      {/* 计数器 */}
      {media.length > 1 && (
        <span className="absolute bottom-4 right-4 rounded-full bg-white/10 px-3 py-1 text-sm text-white">
          {index + 1} / {media.length}
        </span>
      )}
    </div>
  )
}
