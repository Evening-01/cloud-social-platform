import { useState } from "react"
import { cn } from "@/lib/utils"

/**
 * 带加载动画的图片组件（自适应图片实际比例）
 * - 有 aspectRatio：框按比例占位，图片完整显示不裁剪
 * - 无 aspectRatio：图片按自然高度显示，框自动适配
 */
export function LazyImage({
  src,
  alt,
  className,
  style,
  onClick,
  aspectRatio,
}: {
  src: string
  alt: string
  className?: string
  style?: React.CSSProperties
  onClick?: () => void
  aspectRatio?: number
}) {
  const [loaded, setLoaded] = useState(false)
  const [error, setError] = useState(false)

  return (
    <div
      className={cn("relative overflow-hidden bg-[rgb(var(--muted))]", onClick && "cursor-zoom-in", className)}
      style={aspectRatio ? { aspectRatio: String(aspectRatio), ...style } : style}
      onClick={onClick}
    >
      {/* 加载中骨架屏 */}
      {!loaded && !error && (
        <div className="absolute inset-0 overflow-hidden">
          <div className="absolute inset-y-0 w-1/3 animate-shimmer bg-gradient-to-r from-transparent via-white/15 to-transparent" />
        </div>
      )}

      {/* 加载失败占位 */}
      {error && (
        <div className="flex h-48 w-full items-center justify-center text-xs text-[rgb(var(--muted-foreground))]">
          图片加载失败
        </div>
      )}

      {/* 图片：有比例用 cover 填满（比例一致不裁剪），无比例用自然高度 */}
      {!error && (
        <img
          src={src}
          alt={alt}
          loading="lazy"
          onLoad={() => setLoaded(true)}
          onError={() => setError(true)}
          className={cn(
            "block transition-opacity duration-150",
            aspectRatio ? "h-full w-full object-cover" : "h-auto w-full",
            loaded ? "opacity-100" : "opacity-0"
          )}
        />
      )}
    </div>
  )
}
