package com.example.poly.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        return CaffeineCacheManager().apply {
            setCaffeine(caffeineConfig())
            setCacheNames(setOf("problems"))
        }
    }

    private fun caffeineConfig(): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(Duration.ofMinutes(5))
    }
}