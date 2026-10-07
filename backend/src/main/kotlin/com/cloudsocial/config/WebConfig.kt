package com.cloudsocial.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web 配置：静态资源映射
 */
@Configuration
class WebConfig(
    @Value("\${upload.local-storage:./uploads}") private val storageDir: String
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val absolutePath = java.nio.file.Paths.get(storageDir)
            .normalize().toAbsolutePath().toUri().toString()
        // /uploads/** 映射到本地 uploads 目录
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(absolutePath)
    }
}
