import { useEffect, useState, type ReactNode } from "react"

/**
 * JS 瀑布流：按卡片高度权重分配到最矮列，避免 CSS columns 的布局跳动
 */
export function Masonry<T>({
  items,
  render,
  getHeightWeight,
}: {
  items: T[]
  render: (item: T, index: number) => ReactNode
  getHeightWeight: (item: T) => number
}) {
  const [columns, setColumns] = useState(4)

  useEffect(() => {
    const update = () => {
      const w = window.innerWidth
      setColumns(w < 640 ? 2 : w < 1024 ? 3 : 4)
    }
    update()
    window.addEventListener("resize", update)
    return () => window.removeEventListener("resize", update)
  }, [])

  // 分配卡片到最矮列
  const cols: { item: T; index: number }[][] = Array.from({ length: columns }, () => [])
  const heights = new Array(columns).fill(0)
  items.forEach((item, index) => {
    const weight = getHeightWeight(item) + 0.35 // 0.35 为文字区高度权重
    const minIdx = heights.indexOf(Math.min(...heights))
    cols[minIdx].push({ item, index })
    heights[minIdx] += weight
  })

  return (
    <div className="flex items-start gap-4">
      {cols.map((col, i) => (
        <div key={i} className="flex min-w-0 flex-1 flex-col gap-4">
          {col.map(({ item, index }) => render(item, index))}
        </div>
      ))}
    </div>
  )
}
