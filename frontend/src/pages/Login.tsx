import { useState } from "react"
import { authApi } from "@/lib/api"
import { Button, Input } from "@/components/ui"

export function Login() {
  const [mode, setMode] = useState<"login" | "register">("login")
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [nickname, setNickname] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const submit = async () => {
    setError("")
    if (!username || !password) {
      setError("请填写用户名和密码")
      return
    }
    if (mode === "register" && !nickname) {
      setError("请填写昵称")
      return
    }
    setLoading(true)
    try {
      if (mode === "register") {
        await authApi.register({ username, password, nickname })
        setMode("login")
        setError("注册成功，请登录")
        setPassword("")
        return
      }
      const res = await authApi.login({ username, password })
      localStorage.setItem("token", res.token)
      localStorage.setItem("user", JSON.stringify(res.user))
      window.location.href = "/"
    } catch (e: any) {
      setError(e.response?.data?.message || e.message || "操作失败")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-[rgb(var(--primary))]/10 to-[rgb(var(--background))] p-4">
      <div className="w-full max-w-sm rounded-3xl border border-[rgb(var(--border))] bg-[rgb(var(--card))] p-8 shadow-xl">
        <h1 className="mb-1 text-center text-3xl font-bold text-[rgb(var(--primary))]">云社</h1>
        <p className="mb-6 text-center text-sm text-[rgb(var(--muted-foreground))]">图文视频分享社区</p>

        <div className="mb-6 flex rounded-full bg-[rgb(var(--muted))] p-1">
          {(["login", "register"] as const).map((m) => (
            <button
              key={m}
              onClick={() => setMode(m)}
              className={`flex-1 rounded-full py-2 text-sm font-medium transition-all ${
                mode === m ? "bg-white text-[rgb(var(--foreground))] shadow" : "text-[rgb(var(--muted-foreground))]"
              }`}
            >
              {m === "login" ? "登录" : "注册"}
            </button>
          ))}
        </div>

        <div className="space-y-3">
          <Input placeholder="用户名" value={username} onChange={(e) => setUsername(e.target.value)} />
          <Input placeholder="密码" type="password" value={password} onChange={(e) => setPassword(e.target.value)} onKeyDown={(e) => e.key === "Enter" && submit()} />
          {mode === "register" && <Input placeholder="昵称" value={nickname} onChange={(e) => setNickname(e.target.value)} />}
          {error && <p className="text-sm text-[rgb(var(--primary))]">{error}</p>}
          <Button className="w-full py-2.5" onClick={submit} disabled={loading}>
            {loading ? "处理中..." : mode === "login" ? "登录" : "注册"}
          </Button>
          <a href="/" className="block text-center text-sm text-[rgb(var(--muted-foreground))] hover:underline">
            先随便逛逛 →
          </a>
        </div>
      </div>
    </div>
  )
}
