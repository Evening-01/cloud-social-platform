import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import { Heart, Share2 } from "lucide-react"
import { contentApi, likeApi, shareApi, type Content } from "@/lib/api"
import { Button, Dialog, Skeleton } from "@/components/ui"
import { formatCount, formatTime } from "@/lib/utils"
import { ThemeToggle } from "@/lib/theme"
import { Lightbox } from "@/components/Lightbox"

export function Detail() {
  const { id } = useParams()
  const [content, setContent] = useState<Content | null>(null)
  const [loading, setLoading] = useState(true)
  const [liked, setLiked] = useState(false)
  const [likeCount, setLikeCount] = useState(0)
  const [shareOpen, setShareOpen] = useState(false)
  const [shareUrl, setShareUrl] = useState("")
  const [lightbox, setLightbox] = useState<number | null>(null)

  useEffect(() => {
    if (!id) return
    contentApi
      .detail(Number(id))
      .then((c) => {
        setContent(c)
        setLiked(c.liked)
        setLikeCount(c.likeCount)
      })
      .finally(() => setLoading(false))
  }, [id])

  const toggleLike = async () => {
    if (!content) return
    const prev = liked
    setLiked(!prev)
    setLikeCount((n) => n + (prev ? -1 : 1))
    try {
      const c = prev ? await likeApi.unlike(content.id) : await likeApi.like(content.id)
      setLikeCount(c)
    } catch {
      setLiked(prev)
    }
  }

  const handleShare = async () => {
    if (!content) return
    setShareOpen(true)
    try {
      const res = await shareApi.create(content.id, 0)
      setShareUrl(`${window.location.origin}/s/${res.shortCode}`)
    } catch {}
  }

  if (loading) {
    return (
      <div className="mx-auto max-w-2xl px-4 py-8">
        <Skeleton className="mb-4 h-8 w-2/3" />
        <Skeleton className="mb-6 h-96" />
      </div>
    )
  }

  if (!content) return <div className="p-8 text-center">内容不存在</div>

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-40 border-b border-[rgb(var(--border))] bg-[rgb(var(--background))]/90 backdrop-blur">
        <div className="mx-auto flex max-w-2xl items-center px-4 py-3">
          <a href="/" className="text-lg font-bold text-[rgb(var(--primary))]">← 返回</a>
          <div className="ml-auto"><ThemeToggle /></div>
        </div>
      </header>

      <main className="mx-auto max-w-2xl px-4 py-6">
        <h1 className="mb-2 text-2xl font-bold">{content.title}</h1>
        <div className="mb-4 flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-[rgb(var(--primary))] text-sm text-white">
            {(content.author?.nickname ?? "用").charAt(0)}
          </div>
          <div>
            <p className="text-sm font-medium">{content.author?.nickname ?? "匿名"}</p>
            <p className="text-xs text-[rgb(var(--muted-foreground))]">{formatTime(content.createdAt)}</p>
          </div>
        </div>

        {content.description && <p className="mb-4 text-sm text-[rgb(var(--muted-foreground))]">{content.description}</p>}

        {/* 媒体列表 */}
        <div className="space-y-3">
          {content.mediaFiles.map((m, i) =>
            m.mediaType === 2 ? (
              <video key={m.id} src={m.url} poster={m.coverUrl ?? undefined} controls className="w-full rounded-2xl" />
            ) : (
              <img key={m.id} src={m.url} className="w-full cursor-zoom-in rounded-2xl" onClick={() => setLightbox(i)} />
            )
          )}
        </div>

        {/* 操作栏 */}
        <div className="sticky bottom-0 mt-6 flex items-center justify-center gap-8 rounded-2xl border border-[rgb(var(--border))] bg-[rgb(var(--card))] p-4 shadow-lg">
          <button onClick={toggleLike} className={`flex items-center gap-2 text-lg ${liked ? "text-[rgb(var(--primary))]" : ""}`}>
            <Heart className={`h-7 w-7 ${liked ? "fill-current" : ""}`} />
            <span>{formatCount(likeCount)}</span>
          </button>
          <button onClick={handleShare} className="flex items-center gap-2 text-lg text-[rgb(var(--muted-foreground))]">
            <Share2 className="h-6 w-6" />
          </button>
        </div>
      </main>

      <Dialog open={shareOpen} onClose={() => setShareOpen(false)} title="分享链接">
        {shareUrl ? (
          <div className="flex items-center gap-2">
            <input readOnly value={shareUrl} className="w-full rounded-xl border border-[rgb(var(--border))] px-3 py-2 text-xs" />
            <Button onClick={() => navigator.clipboard.writeText(shareUrl)}>复制</Button>
          </div>
        ) : (
          <p className="text-sm">生成中...</p>
        )}
      </Dialog>

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
