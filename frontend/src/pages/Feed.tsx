import { useEffect, useState } from "react"
import { Plus, Search } from "lucide-react"
import { contentApi, type Content } from "@/lib/api"
import { ContentCard } from "@/components/ContentCard"
import { Button, Input, Skeleton } from "@/components/ui"

export function Feed() {
  const [items, setItems] = useState<Content[]>([])
  const [page, setPage] = useState(0)
  const [hasMore, setHasMore] = useState(true)
  const [loading, setLoading] = useState(true)
  const [keyword, setKeyword] = useState("")
  const user = JSON.parse(localStorage.getItem("user") || "null")

  const load = async (p: number, kw: string = "", append = false) => {
    setLoading(true)
    try {
      const res = await contentApi.feed(p, 10, kw || undefined)
      setItems((prev) => (append ? [...prev, ...res.items] : res.items))
      setHasMore(res.hasMore)
      setPage(p)
    } catch (e) {
      console.error(e)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load(0)
  }, [])

  useEffect(() => {
    const onScroll = () => {
      if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 600 && hasMore && !loading) {
        load(page + 1, keyword, true)
      }
    }
    window.addEventListener("scroll", onScroll)
    return () => window.removeEventListener("scroll", onScroll)
  }, [page, hasMore, loading, keyword])

  return (
    <div className="min-h-screen">
      {/* 顶部导航 */}
      <header className="sticky top-0 z-40 border-b border-[rgb(var(--border))] bg-[rgb(var(--background))]/90 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center gap-4 px-4 py-3">
          <h1 className="text-xl font-bold text-[rgb(var(--primary))]">云社</h1>
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[rgb(var(--muted-foreground))]" />
            <Input
              placeholder="搜索内容..."
              className="pl-9"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") load(0, keyword)
              }}
            />
          </div>
          <div className="ml-auto flex items-center gap-2">
            {user ? (
              <>
                <span className="hidden text-sm sm:block">{user.nickname}</span>
                <a href="/publish">
                  <Button>
                    <Plus className="h-4 w-4" /> 发布
                  </Button>
                </a>
              </>
            ) : (
              <a href="/login">
                <Button variant="outline">登录</Button>
              </a>
            )}
          </div>
        </div>
      </header>

      {/* 瀑布流 */}
      <main className="mx-auto max-w-6xl px-4 py-6">
        {loading && items.length === 0 ? (
          <div className="columns-2 gap-4 md:columns-3 lg:columns-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <Skeleton key={i} className="mb-4 h-64" />
            ))}
          </div>
        ) : items.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-24 text-[rgb(var(--muted-foreground))]">
            <p className="mb-2 text-4xl">🌊</p>
            <p>还没有内容，快来发布第一篇吧～</p>
          </div>
        ) : (
          <div className="columns-2 gap-4 md:columns-3 lg:columns-4">
            {items.map((c) => (
              <ContentCard key={c.id} content={c} />
            ))}
          </div>
        )}
        {loading && items.length > 0 && <p className="py-4 text-center text-sm text-[rgb(var(--muted-foreground))]">加载中...</p>}
        {!hasMore && items.length > 0 && (
          <p className="py-6 text-center text-sm text-[rgb(var(--muted-foreground))]">— 到底啦 —</p>
        )}
      </main>
    </div>
  )
}
