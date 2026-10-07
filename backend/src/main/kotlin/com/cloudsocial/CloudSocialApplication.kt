package com.cloudsocial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Cloud Social Platform 后端启动类
 */
@SpringBootApplication
@EnableScheduling
class CloudSocialApplication

fun main(args: Array<String>) {
    runApplication<CloudSocialApplication>(*args)
}
