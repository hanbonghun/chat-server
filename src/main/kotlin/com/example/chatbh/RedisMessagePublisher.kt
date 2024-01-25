package com.example.chatbh

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessagePublisher(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {

    fun publish(message: String) {
        reactiveRedisTemplate.convertAndSend("chatTopic", message).subscribe()
    }
}