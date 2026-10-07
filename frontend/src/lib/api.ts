import axios from "axios"

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface User {
  id: number
  username: string
  nickname: string
  avatarUrl: string | null
  email: string | null
}

export interface MediaFile {
  id: number
  mediaType: number
  url: string
  coverUrl: string | null
  width: number | null
  height: number | null
  durationSec: number | null
  sortOrder: number
}

export interface Content {
  id: number
  userId: number
  author: User | null
  title: string
  description: string | null
  contentType: number
  likeCount: number
  liked: boolean
  mediaFiles: MediaFile[]
  createdAt: string
}

export interface Comment {
  id: number
  userId: number
  contentId: number
  content: string
  author: User | null
  createdAt: string
}

export interface PageResponse<T> {
  items: T[]
  page: number
  size: number
  total: number
  hasMore: boolean
}

const api = axios.create({
  baseURL: "/api",
  timeout: 60000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token")
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem("token")
      localStorage.removeItem("user")
      if (!window.location.pathname.startsWith("/login")) {
        window.location.href = "/login"
      }
    }
    return Promise.reject(err)
  }
)

export async function request<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const res = await promise
  if (res.data.code !== 20000) {
    throw new Error(res.data.message || "请求失败")
  }
  return res.data.data
}

export const authApi = {
  register: (data: { username: string; password: string; nickname: string }) =>
    request(api.post("/auth/register", data)),
  login: (data: { username: string; password: string }) =>
    request(api.post<ApiResponse<{ token: string; user: User }>>("/auth/login", data)),
  me: () => request(api.get("/auth/me")),
}

export const contentApi = {
  create: (formData: FormData) =>
    request(api.post<ApiResponse<Content>>("/content", formData)),
  feed: (page = 0, size = 10, keyword?: string) =>
    request(
      api.get<ApiResponse<PageResponse<Content>>>("/content", {
        params: { page, size, keyword },
      })
    ),
  detail: (id: number) => request(api.get<ApiResponse<Content>>(`/content/${id}`)),
}

export const likeApi = {
  like: (contentId: number) => request(api.post<ApiResponse<number>>(`/like/${contentId}`)),
  unlike: (contentId: number) => request(api.delete<ApiResponse<number>>(`/like/${contentId}`)),
  count: (contentId: number) => request(api.get<ApiResponse<number>>(`/like/${contentId}/count`)),
}

export interface Comment {
  id: number
  contentId: number
  userId: number
  author: User | null
  content: string
  createdAt: string
}

export const commentApi = {
  list: (contentId: number) => request(api.get<ApiResponse<Comment[]>>(`/comment/${contentId}`)),
  count: (contentId: number) => request(api.get<ApiResponse<number>>(`/comment/${contentId}/count`)),
  create: (contentId: number, content: string) =>
    request(api.post<ApiResponse<Comment>>(`/comment/${contentId}`, { content })),
  remove: (commentId: number) => request(api.delete<ApiResponse<null>>(`/comment/${commentId}`)),
}

export const shareApi = {
  create: (contentId: number, expireDays = 0) =>
    request(api.post<ApiResponse<{ shortCode: string }>>(`/share/${contentId}`, null, { params: { expireDays } })),
  resolve: (code: string) => request(api.get<ApiResponse<Content>>(`/share/${code}`)),
}

export default api
