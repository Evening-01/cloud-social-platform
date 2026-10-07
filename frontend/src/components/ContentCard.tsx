import { useEffect, useRef, useState } from "react"
import { Heart, Share2, MessageCircle } from "lucide-react"
import type { Content } from "@/lib/api"
import { likeApi, shareApi, commentApi } from "@/lib/api"
import { cn, formatCount, formatTime } from "@/lib/utils"
import { Dialog, Button } from "@/components/ui"
import { Lightbox } from "@/components/Lightbox"
import { CommentDialog } from "@/components/CommentDialog"
import { LazyImage } from "@/components/LazyImage"

export function ContentCard({ content }: { content: Content }) {
  const [liked, setLiked] = useState(content.liked)
  const [likeCount, setLikeCount] = useState(content.likeCount)
  const [liking, setLiking] = useState(false)
  const [shareOpen, setShareOpen] = useState(false)
  const [shareCode, setShareCode] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)
  const [commentOpen, setCommentOpen] = useState(false)
  const [commentCount, setCommentCount] = useState(0)
  const [lightbox, setLightbox] = useState<number | null>(null)
  const [hearts, setHearts] = useState<{ id: number; x: number }[]>([])
  const videoRef = useRef<HTMLVideoElement>(null)

  const toggleLike = async (e: React.MouseEvent) => {
    if (liking) return
    if (!localStorage.getItem("token")) {
      window.location.href = "/login"
      return
    }
    setLiking(true)
    const prevLiked = liked
    const prevCount = likeCount
    setLiked(!prevLiked)
    setLikeCount(prevCount + (prevLiked ? -1 : 1))

    // 飘心特效（仅点赞时）
    if (!prevLiked) {
      const btn = (e.currentTarget as HTMLElement).getBoundingClientRect()
      const id = Date.now()
      setHearts((h) => [...h, { id, x: e.clientX - btn.left }])
      setTimeout(() => setHearts((h) => h.filter((x) => x.id !== id)), 1000)
    }

    try {
      if (prevLiked) {
        const c = await likeApi.unlike(content.id)
        setLikeCount(c)
      } else {
        const c = await likeApi.like(content.id)
        setLikeCount(c)
      }
    } catch {
      setLiked(prevLiked)
      setLikeCount(prevCount)
    } finally {
      setLiking(false)
    }
  }

  const handleShare = async () => {
    setShareOpen(true)
    try {
      const res = await shareApi.create(content.id, 0)
      const origin = window.location.origin
      setShareCode(`${origin}/s/${res.shortCode}`)
    } catch {
      setShareCode(null)
    }
  }

  const firstMedia = content.mediaFiles[0]

  useEffect(() => {
    commentApi.count(content.id).then(setCommentCount).catch(() => {})
  }, [content.id])

  return (
    <div className="mb-4 break-inside-avoid rounded-2xl border border-[rgb(var(--border))] bg-[rgb(var(--card))] overflow-hidden shadow-sm hover:shadow-md transition-shadow">
      {/* 媒体区 */}
      {firstMedia && (
        <div
          className="relative bg-[rgb(var(--muted))] cursor-zoom-in"
          onClick={() => firstMedia.mediaType === 1 && setLightbox(0)}
        >
          {firstMedia.mediaType === 2 ? (
            <video
              ref={videoRef}
              src={firstMedia.url}
              poster={firstMedia.coverUrl ?? undefined}
              controls
              preload="metadata"
              muted
              loop
              playsInline
              className="w-full object-cover"
              onMouseEnter={(e) => e.currentTarget.play().catch(() => {})}
              onMouseLeave={(e) => e.currentTarget.pause()}
            />
          ) : (
            <LazyImage
              src={firstMedia.url}
              alt={content.title}
              className="w-full"
              aspectRatio={firstMedia.width && firstMedia.height ? firstMedia.width / firstMedia.height : undefined}
            />
          )}
          {content.mediaFiles.length > 1 && (
            <span className="absolute top-2 right-2 rounded-full bg-black/60 px-2 py-0.5 text-xs text-white">
              +{content.mediaFiles.length - 1}
            </span>
          )}
          {content.contentType === 2 && (
            <span className="absolute top-2 left-2 rounded-full bg-black/60 px-2 py-0.5 text-xs text-white">视频</span>
          )}
        </div>
      )}

      {/* 内容区 */}
      <div className="p-3">
        <h3 className="mb-2 line-clamp-2 text-sm font-medium leading-snug">{content.title}</h3>
        {content.description && (
          <p className="mb-2 line-clamp-2 text-xs text-[rgb(var(--muted-foreground))]">{content.description}</p>
        )}

        {/* 作者 + 时间 */}
        <div className="mb-2 flex items-center gap-2">
          <div className="flex h-6 w-6 items-center justify-center rounded-full bg-[rgb(var(--primary))] text-xs text-white">
            {(content.author?.nickname ?? "用").charAt(0)}
          </div>
          <span className="text-xs text-[rgb(var(--muted-foreground))]">{content.author?.nickname ?? "匿名"}</span>
          <span className="text-xs text-[rgb(var(--muted-foreground))]">·</span>
          <span className="text-xs text-[rgb(var(--muted-foreground))]">{formatTime(content.createdAt)}</span>
        </div>

        {/* 操作栏 */}
        <div className="flex items-center gap-4">
          <button
            onClick={toggleLike}
            className={cn("relative group flex items-center gap-1 text-sm transition-all", liked && "text-[rgb(var(--primary))]")}
          >
            <Heart
              className={cn("h-5 w-5 transition-transform group-active:scale-125", liked && "fill-current")}
            />
            <span>{formatCount(likeCount)}</span>
            {/* 飘心特效 */}
            {hearts.map((h) => (
              <span
                key={h.id}
                className="pointer-events-none absolute -top-6 animate-heart"
                style={{ left: h.x - 8 }}
              >
                ❤️
              </span>
            ))}
          </button>
          <button onClick={() => setCommentOpen(true)} className="flex items-center gap-1 text-sm text-[rgb(var(--muted-foreground))]">
            <MessageCircle className="h-5 w-5" />
            {commentCount > 0 && <span>{formatCount(commentCount)}</span>}
          </button>
          <button onClick={handleShare} className="flex items-center gap-1 text-sm text-[rgb(var(--muted-foreground))]">
            <Share2 className="h-5 w-5" />
          </button>
        </div>
      </div>

      {/* 分享弹窗 */}
      <Dialog open={shareOpen} onClose={() => setShareOpen(false)} title="分享链接">
        {shareCode ? (
          <div className="space-y-3">
            <p className="text-xs text-[rgb(var(--muted-foreground))]">复制链接分享给好友，对方即可访问点赞：</p>
            <div className="flex items-center gap-2">
              <input readOnly value={shareCode} className="w-full rounded-xl border border-[rgb(var(--border))] px-3 py-2 text-xs" />
              <Button
                onClick={() => {
                  navigator.clipboard.writeText(shareCode).then(() => {
                    setCopied(true)
                    setTimeout(() => setCopied(false), 2000)
                  })
                }}
              >
                {copied ? "已复制" : "复制"}
              </Button>
            </div>
          </div>
        ) : (
          <p className="text-sm text-[rgb(var(--muted-foreground))]">生成中...</p>
        )}
      </Dialog>

      {/* 评论面板 */}
      <CommentDialog contentId={content.id} open={commentOpen} onClose={() => setCommentOpen(false)} />

      {/* 图片预览器 */}
      {lightbox !== null && (
        <Lightbox
          media={content.mediaFiles}
          index={lightbox}
          onClose={() => setLightbox(null)}
          onNavigate={(i) => setLightbox(i)}
        />
      )}
    </div>
  )
}
