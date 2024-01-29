package com.example.chatbh

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

//@Service
//class RedisMessagePublisher(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {
//
//    fun publishToChatRoom(chatRoomId: String, message: String) {
//        val chatRoomTopic = "chatRoom:$chatRoomId"
//        reactiveRedisTemplate.convertAndSend(chatRoomTopic, message).subscribe()
//    }
//}