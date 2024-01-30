package com.example.chatbh.websocket.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import java.util.concurrent.ConcurrentHashMap


@Service
class WebSocketSessionManager {
    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    fun registerSession(sessionId: String, session: WebSocketSession) {
        sessions[sessionId] = session
    }

    fun removeSession(sessionId: String) {
        sessions.remove(sessionId)
    }

    fun getSessionById(sessionId: String): WebSocketSession? {
        return sessions[sessionId]
    }

    fun getUserIdFromSession(session: WebSocketSession): String? {
        val uri = session.handshakeInfo.uri.toString()
        val uriComponents = UriComponentsBuilder.fromUriString(uri).build()
        return uriComponents.queryParams.getFirst("userId")
    }
}