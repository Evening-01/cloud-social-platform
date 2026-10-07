const CACHE = "cloud-social-v1"

self.addEventListener("install", () => {
  self.skipWaiting()
})

self.addEventListener("activate", (e) => {
  e.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k)))
    )
  )
  self.clients.claim()
})

self.addEventListener("fetch", (e) => {
  const url = new URL(e.request.url)
  // 只缓存同源静态资源，跳过 API 和上传文件
  if (
    e.request.method === "GET" &&
    url.origin === location.origin &&
    !url.pathname.startsWith("/api/") &&
    !url.pathname.startsWith("/uploads/")
  ) {
    e.respondWith(
      caches.open(CACHE).then(async (cache) => {
        const cached = await cache.match(e.request)
        if (cached) return cached
        try {
          const res = await fetch(e.request)
          if (res.ok) cache.put(e.request, res.clone())
          return res
        } catch {
          return cached || Response.error()
        }
      })
    )
  }
})
