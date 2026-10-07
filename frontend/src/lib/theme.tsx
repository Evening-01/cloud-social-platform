import { useEffect, useState } from "react"
import { Moon, Sun } from "lucide-react"

function getInitialTheme(): boolean {
  const saved = localStorage.getItem("theme")
  if (saved) return saved === "dark"
  return window.matchMedia("(prefers-color-scheme: dark)").matches
}

export function useTheme() {
  const [dark, setDark] = useState(getInitialTheme)

  useEffect(() => {
    const root = document.documentElement
    if (dark) root.classList.add("dark")
    else root.classList.remove("dark")
    localStorage.setItem("theme", dark ? "dark" : "light")
  }, [dark])

  return { dark, toggle: () => setDark((d) => !d) }
}

export function ThemeToggle() {
  const { dark, toggle } = useTheme()
  return (
    <button
      onClick={toggle}
      aria-label="切换主题"
      className="flex h-9 w-9 items-center justify-center rounded-full hover:bg-[rgb(var(--muted))] transition-colors cursor-pointer"
    >
      {dark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
    </button>
  )
}
