package com.example.chatbh.websocket.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap


@Service
class WebSocketSessionManager {
    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()
    private val userSessions: MutableMap<String, String> = ConcurrentHashMap()
    private val chatRoomSessions: MutableMap<String, MutableSet<String>> = ConcurrentHashMap()

    fun registerSession(sessionId: String, username: String, session: WebSocketSession) {
        sessions[sessionId] = session
        userSessions[username] = sessionId
    }

    fun removeSession(sessionId: String) {
        sessions.remove(sessionId)
        userSessions.values.remove(sessionId)
        chatRoomSessions.values.forEach { it.remove(sessionId) }
    }

    fun addUserToChatRoom(username: String, chatRoomId: String) {
        val sessionId = userSessions[username] ?: return
        chatRoomSessions.computeIfAbsent(chatRoomId) { ConcurrentHashMap.newKeySet() }.add(sessionId)
    }

    fun getSessionsForChatRoom(chatRoomId: String): Set<WebSocketSession> {
        return chatRoomSessions[chatRoomId]?.mapNotNull { sessions[it] }?.toSet() ?: emptySet()
    }

    fun getSessionById(sessionId: String): WebSocketSession? {
        return sessions[sessionId]
    }
}