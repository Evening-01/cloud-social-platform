import { cn } from "@/lib/utils"
import { forwardRef, type ButtonHTMLAttributes, type InputHTMLAttributes, type TextareaHTMLAttributes, type HTMLAttributes } from "react"

/* ---------- Button ---------- */
type ButtonVariant = "default" | "ghost" | "outline" | "secondary"

export const Button = forwardRef<HTMLButtonElement, ButtonHTMLAttributes<HTMLButtonElement> & { variant?: ButtonVariant }>(
  function Button({ className, variant = "default", ...props }, ref) {
    const variants: Record<ButtonVariant, string> = {
      default: "bg-[rgb(var(--primary))] text-white hover:opacity-90",
      ghost: "hover:bg-[rgb(var(--muted))]",
      outline: "border border-[rgb(var(--border))] hover:bg-[rgb(var(--muted))]",
      secondary: "bg-[rgb(var(--muted))] hover:opacity-80",
    }
    return (
      <button
        ref={ref}
        className={cn(
          "inline-flex items-center justify-center gap-2 rounded-full px-4 py-2 text-sm font-medium transition-all active:scale-95 disabled:opacity-50 disabled:pointer-events-none cursor-pointer",
          variants[variant],
          className
        )}
        {...props}
      />
    )
  }
)

/* ---------- Card ---------- */
export function Card({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("rounded-2xl bg-[rgb(var(--card))] border border-[rgb(var(--border))] overflow-hidden", className)}
      {...props}
    />
  )
}

/* ---------- Input ---------- */
export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(function Input(
  { className, ...props },
  ref
) {
  return (
    <input
      ref={ref}
      className={cn(
        "w-full rounded-xl border border-[rgb(var(--border))] bg-transparent px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-[rgb(var(--ring))]/40 placeholder:text-[rgb(var(--muted-foreground))]",
        className
      )}
      {...props}
    />
  )
})

/* ---------- Textarea ---------- */
export const Textarea = forwardRef<HTMLTextAreaElement, TextareaHTMLAttributes<HTMLTextAreaElement>>(function Textarea(
  { className, ...props },
  ref
) {
  return (
    <textarea
      ref={ref}
      className={cn(
        "w-full rounded-xl border border-[rgb(var(--border))] bg-transparent px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-[rgb(var(--ring))]/40 resize-none",
        className
      )}
      {...props}
    />
  )
})

/* ---------- Badge ---------- */
export function Badge({ className, ...props }: HTMLAttributes<HTMLSpanElement>) {
  return (
    <span
      className={cn("inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium bg-[rgb(var(--muted))]", className)}
      {...props}
    />
  )
}

/* ---------- Skeleton ---------- */
export function Skeleton({ className }: { className?: string }) {
  return <div className={cn("animate-pulse rounded-xl bg-[rgb(var(--muted))]", className)} />
}

/* ---------- Dialog ---------- */
export function Dialog({ open, onClose, children, title }: { open: boolean; onClose: () => void; children: React.ReactNode; title?: string }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={onClose} />
      <div className="relative w-full max-w-md rounded-2xl bg-[rgb(var(--card))] p-6 shadow-xl">
        {title && <h2 className="mb-4 text-lg font-semibold">{title}</h2>}
        {children}
      </div>
    </div>
  )
}
