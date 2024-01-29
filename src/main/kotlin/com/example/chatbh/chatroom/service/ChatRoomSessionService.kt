package com.example.chatbh.chatroom.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

// (chatRoomSessions:room1) -> ["session1", "session2"]
@Service
class ChatRoomSessionService (private val redisTemplate: RedisTemplate<String, String>) {
    fun addSessionToChatRoom(sessionId: String, chatRoomId: String) {
        redisTemplate.opsForSet().add(getChatRoomKey(chatRoomId), sessionId)
        println("Added session $sessionId to chat room $chatRoomId")
    }

    fun getSessionsInChatRoom(chatRoomId: String): Set<String>? {
        return redisTemplate.opsForSet().members(getChatRoomKey(chatRoomId))
    }

    fun removeSessionFromChatRoom(sessionId: String?, chatRoomId: String) {
        redisTemplate.opsForSet().remove(getChatRoomKey(chatRoomId), sessionId)
    }

    private fun getChatRoomKey(chatRoomId: String): String {
        return CHATROOM_SESSION_KEY_PREFIX + chatRoomId
    }

    companion object {
        private const val CHATROOM_SESSION_KEY_PREFIX = "chatRoomSessions:"
    }
}