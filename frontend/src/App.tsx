import { Routes, Route } from "react-router-dom"
import { Feed } from "@/pages/Feed"
import { Login } from "@/pages/Login"
import { Publish } from "@/pages/Publish"
import { Detail } from "@/pages/Detail"
import { ShareLanding } from "@/pages/ShareLanding"

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Feed />} />
      <Route path="/login" element={<Login />} />
      <Route path="/publish" element={<Publish />} />
      <Route path="/content/:id" element={<Detail />} />
      <Route path="/s/:code" element={<ShareLanding />} />
    </Routes>
  )
}
