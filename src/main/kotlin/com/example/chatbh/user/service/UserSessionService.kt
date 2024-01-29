package com.example.chatbh.user.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

// (userSessions) -> {"user1": "session1"}
@Service
class UserSessionService(
    private val redisTemplate: RedisTemplate<String, String>?
) {
    private val USER_SESSION_KEY = "userSessions"

    fun mapSessionToUser(sessionId: String, userId: String) {
        redisTemplate!!.opsForHash<Any, Any>().put(USER_SESSION_KEY, userId, sessionId)
        println("Mapped session $sessionId to user $userId")

    }

    fun getSessionIdByUser(userId: String?): String? {
        return redisTemplate!!.opsForHash<Any, Any>()[USER_SESSION_KEY, userId!!] as String?
    }

    fun removeSessionMapping(userId: String?) {
        redisTemplate!!.opsForHash<Any, Any>().delete(USER_SESSION_KEY, userId)
    }
}