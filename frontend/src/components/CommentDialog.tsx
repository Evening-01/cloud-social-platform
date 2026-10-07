import { useEffect, useRef, useState } from "react"
import { Send, Trash2 } from "lucide-react"
import { commentApi, type Comment } from "@/lib/api"
import { Button, Dialog, Input, Skeleton } from "@/components/ui"
import { formatTime } from "@/lib/utils"

/** 评论弹窗：列表 + 发表 + 删除 */
export function CommentDialog({ contentId, open, onClose }: { contentId: number; open: boolean; onClose: () => void }) {
  const [comments, setComments] = useState<Comment[]>([])
  const [loading, setLoading] = useState(true)
  const [text, setText] = useState("")
  const [sending, setSending] = useState(false)
  const loadedRef = useRef<number | null>(null)
  const user = JSON.parse(localStorage.getItem("user") || "null")

  const load = () => {
    // 同一内容已加载过则用缓存，避免重复请求
    if (loadedRef.current === contentId) return
    setLoading(true)
    commentApi
      .list(contentId)
      .then((list) => {
        setComments(list)
        loadedRef.current = contentId
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    // 挂载即预加载评论，点开弹窗时数据已就绪（不卡）
    load()
  }, [contentId])

  const submit = async () => {
    if (!text.trim() || sending) return
    setSending(true)
    try {
      const c = await commentApi.create(contentId, text.trim())
      setComments((prev) => [...prev, c])
      setText("")
    } catch (e: any) {
      alert(e.message || "评论失败")
    } finally {
      setSending(false)
    }
  }

  const remove = async (id: number) => {
    try {
      await commentApi.remove(id)
      setComments((prev) => prev.filter((c) => c.id !== id))
    } catch (e: any) {
      alert(e.message || "删除失败")
    }
  }

  return (
    <Dialog open={open} onClose={onClose} title="评论">
      <div className="space-y-3">
        {/* 评论列表 */}
        <div className="max-h-72 space-y-3 overflow-y-auto pr-1">
          {loading ? (
            Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} className="h-10" />)
          ) : comments.length === 0 ? (
            <p className="py-6 text-center text-sm text-[rgb(var(--muted-foreground))]">还没有评论，来抢沙发～</p>
          ) : (
            comments.map((c) => (
              <div key={c.id} className="flex gap-2">
                <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-[rgb(var(--primary))] text-xs text-white">
                  {(c.author?.nickname ?? "匿").charAt(0)}
                </div>
                <div className="flex-1">
                  <div className="flex items-baseline gap-2">
                    <span className="text-xs font-medium">{c.author?.nickname ?? "匿名"}</span>
                    <span className="text-[10px] text-[rgb(var(--muted-foreground))]">{formatTime(c.createdAt)}</span>
                  </div>
                  <p className="mt-0.5 break-words text-sm">{c.content}</p>
                </div>
                {user && c.userId === user.id && (
                  <button
                    className="self-start text-[rgb(var(--muted-foreground))] hover:text-[rgb(var(--primary))]"
                    onClick={() => remove(c.id)}
                    aria-label="删除评论"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                )}
              </div>
            ))
          )}
        </div>

        {/* 输入框 */}
        {user ? (
          <div className="flex items-center gap-2">
            <Input
              placeholder="说点什么..."
              value={text}
              onChange={(e) => setText(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && submit()}
            />
            <Button onClick={submit} disabled={sending || !text.trim()} aria-label="发送评论">
              <Send className="h-4 w-4" />
            </Button>
          </div>
        ) : (
          <p className="text-center text-xs text-[rgb(var(--muted-foreground))]">
            <a href="/login" className="text-[rgb(var(--primary))] hover:underline">登录</a> 后参与评论
          </p>
        )}
      </div>
    </Dialog>
  )
}
