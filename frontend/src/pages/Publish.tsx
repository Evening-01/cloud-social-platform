import { useState } from "react"
import { ImagePlus, X } from "lucide-react"
import { contentApi } from "@/lib/api"
import { Button, Input, Textarea } from "@/components/ui"

export function Publish() {
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [files, setFiles] = useState<File[]>([])
  const [previews, setPreviews] = useState<string[]>([])
  const [error, setError] = useState("")
  const [publishing, setPublishing] = useState(false)

  const pickFiles = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = Array.from(e.target.files || [])
    if (selected.length + files.length > 9) {
      setError("最多上传 9 个文件")
      return
    }
    setError("")
    setFiles((prev) => [...prev, ...selected])
    setPreviews((prev) => [...prev, ...selected.map((f) => URL.createObjectURL(f))])
  }

  const removeFile = (i: number) => {
    setFiles((prev) => prev.filter((_, idx) => idx !== i))
    setPreviews((prev) => prev.filter((_, idx) => idx !== i))
  }

  const submit = async () => {
    if (!title.trim()) {
      setError("请填写标题")
      return
    }
    if (files.length === 0) {
      setError("请至少上传一个文件")
      return
    }
    setPublishing(true)
    setError("")
    try {
      const formData = new FormData()
      formData.append("title", title)
      formData.append("description", description)
      files.forEach((f) => formData.append("files", f))
      await contentApi.create(formData)
      window.location.href = "/"
    } catch (e: any) {
      setError(e.response?.data?.message || e.message || "发布失败")
    } finally {
      setPublishing(false)
    }
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-40 border-b border-[rgb(var(--border))] bg-[rgb(var(--background))]/90 backdrop-blur">
        <div className="mx-auto flex max-w-2xl items-center px-4 py-3">
          <a href="/" className="text-lg font-bold text-[rgb(var(--primary))]">← 返回</a>
          <h1 className="ml-4 font-semibold">发布内容</h1>
        </div>
      </header>

      <main className="mx-auto max-w-2xl px-4 py-6">
        <div className="space-y-4">
          <Input placeholder="标题" value={title} onChange={(e) => setTitle(e.target.value)} />
          <Textarea placeholder="描述（可选）" rows={4} value={description} onChange={(e) => setDescription(e.target.value)} />

          {/* 文件预览 */}
          {previews.length > 0 && (
            <div className="grid grid-cols-3 gap-2">
              {previews.map((p, i) => (
                <div key={i} className="relative aspect-square overflow-hidden rounded-xl bg-[rgb(var(--muted))]">
                  {files[i].type.startsWith("video") ? (
                    <video src={p} className="h-full w-full object-cover" />
                  ) : (
                    <img src={p} className="h-full w-full object-cover" />
                  )}
                  <button
                    onClick={() => removeFile(i)}
                    className="absolute top-1 right-1 rounded-full bg-black/60 p-1 text-white"
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              ))}
            </div>
          )}

          {/* 上传按钮 */}
          <label className="flex cursor-pointer flex-col items-center justify-center rounded-2xl border-2 border-dashed border-[rgb(var(--border))] py-8 text-[rgb(var(--muted-foreground))] hover:bg-[rgb(var(--muted))]">
            <ImagePlus className="mb-2 h-8 w-8" />
            <span className="text-sm">上传图片 / 视频（最多 9 个）</span>
            <span className="mt-1 text-xs">图片 ≤10MB，视频 ≤500MB</span>
            <input type="file" accept="image/*,video/*" multiple hidden onChange={pickFiles} />
          </label>

          {error && <p className="text-sm text-[rgb(var(--primary))]">{error}</p>}

          <Button className="w-full py-3" onClick={submit} disabled={publishing}>
            {publishing ? "发布中..." : "发布"}
          </Button>
        </div>
      </main>
    </div>
  )
}
