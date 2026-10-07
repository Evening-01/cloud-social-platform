import { useState } from "react"
import { cn } from "@/lib/utils"

/**
 * 带加载动画的图片组件
 * - 加载中：骨架屏 shimmer 动画
 * - 加载完成：淡入显示
 * - 加载失败：占位提示
 */
export function LazyImage({
  src,
  alt,
  className,
  style,
  onClick,
}: {
  src: string
  alt: string
  className?: string
  style?: React.CSSProperties
  onClick?: () => void
}) {
  const [loaded, setLoaded] = useState(false)
  const [error, setError] = useState(false)

  return (
    <div
      className={cn("relative overflow-hidden bg-[rgb(var(--muted))]", onClick && "cursor-zoom-in", className)}
      style={style}
      onClick={onClick}
    >
      {/* 加载中骨架屏 */}
      {!loaded && !error && (
        <div className="absolute inset-0 animate-shimmer bg-gradient-to-r from-[rgb(var(--muted))] via-[rgb(var(--card))] to-[rgb(var(--muted))] bg-[length:200%_100%]" />
      )}

      {/* 加载失败占位 */}
      {error && (
        <div className="absolute inset-0 flex items-center justify-center text-xs text-[rgb(var(--muted-foreground))]">
          图片加载失败
        </div>
      )}

      {/* 图片 */}
      {!error && (
        <img
          src={src}
          alt={alt}
          loading="lazy"
          onLoad={() => setLoaded(true)}
          onError={() => setError(true)}
          className={cn(
            "h-full w-full object-cover transition-opacity duration-500",
            loaded ? "opacity-100" : "opacity-0"
          )}
        />
      )}
    </div>
  )
}
