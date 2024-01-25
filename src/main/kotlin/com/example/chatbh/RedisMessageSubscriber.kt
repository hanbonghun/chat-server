package com.example.chatbh

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class RedisMessageSubscriber(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {

    fun getMessages(): Flux<String> {
        return reactiveRedisTemplate.listenTo(ChannelTopic("chatTopic")).map { it.message }
    }
}