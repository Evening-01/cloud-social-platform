import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import { Heart } from "lucide-react"
import { shareApi, likeApi, type Content } from "@/lib/api"
import { Skeleton } from "@/components/ui"
import { LazyImage } from "@/components/LazyImage"
import { formatCount } from "@/lib/utils"

/** 分享落地页（公开访问） */
export function ShareLanding() {
  const { code } = useParams()
  const [content, setContent] = useState<Content | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [liked, setLiked] = useState(false)
  const [likeCount, setLikeCount] = useState(0)

  useEffect(() => {
    if (!code) return
    shareApi
      .resolve(code)
      .then((c) => {
        setContent(c)
        setLikeCount(c.likeCount)
      })
      .catch((e) => setError(e.message || "链接无效或已过期"))
      .finally(() => setLoading(false))
  }, [code])

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

  if (loading) {
    return (
      <div className="mx-auto max-w-md px-4 py-10">
        <Skeleton className="mb-4 h-8 w-2/3" />
        <Skeleton className="mb-6 h-72" />
      </div>
    )
  }

  if (error || !content) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center p-8 text-center">
        <p className="mb-2 text-4xl">🔗</p>
        <p className="text-lg font-medium">{error || "内容不存在"}</p>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-md px-4 py-8">
      <p className="mb-4 text-center text-xs text-[rgb(var(--muted-foreground))]">来自「云社」的分享</p>
      <div className="overflow-hidden rounded-3xl border border-[rgb(var(--border))] bg-[rgb(var(--card))] shadow-xl">
        {content.mediaFiles[0] && (
          content.mediaFiles[0].mediaType === 2 ? (
            <video src={content.mediaFiles[0].url} poster={content.mediaFiles[0].coverUrl ?? undefined} controls className="w-full" />
          ) : (
            <LazyImage
              src={content.mediaFiles[0].url}
              alt=""
              className="w-full"
              aspectRatio={content.mediaFiles[0].width && content.mediaFiles[0].height ? content.mediaFiles[0].width / content.mediaFiles[0].height : undefined}
            />
          )
        )}
        <div className="p-5">
          <h1 className="mb-2 text-xl font-bold">{content.title}</h1>
          {content.description && <p className="mb-4 text-sm text-[rgb(var(--muted-foreground))]">{content.description}</p>}
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[rgb(var(--primary))] text-white">
              {(content.author?.nickname ?? "用").charAt(0)}
            </div>
            <div className="flex-1">
              <p className="font-medium">{content.author?.nickname ?? "匿名"}</p>
              <p className="text-xs text-[rgb(var(--muted-foreground))]">{content.mediaFiles.length} 个媒体</p>
            </div>
            <button
              onClick={toggleLike}
              className={`flex items-center gap-1.5 rounded-full px-4 py-2 text-sm font-medium transition-all active:scale-95 ${
                liked ? "bg-[rgb(var(--primary))] text-white" : "border border-[rgb(var(--border))]"
              }`}
            >
              <Heart className={`h-5 w-5 ${liked ? "fill-current" : ""}`} />
              {formatCount(likeCount)}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
