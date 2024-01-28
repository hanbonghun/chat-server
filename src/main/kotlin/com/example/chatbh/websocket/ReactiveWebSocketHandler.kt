package com.example.chatbh.websocket

import com.example.chatbh.websocket.service.WebSocketSessionManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ReactiveWebSocketHandler(
    private val sessionManager: WebSocketSessionManager,
) : WebSocketHandler {
    private val objectMapper = jacksonObjectMapper()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val username = getUsernameFromSession(session)
        println("Connected user: $username")

        // 세션 등록
        username?.let { sessionManager.registerSession(session.id, it, session) }

        val outputMessages = session.receive()
            .map { msg -> handleMessage(msg.payloadAsText, session) }
            .then()

        return outputMessages.doFinally {
            // 세션 해제
            sessionManager.removeSession(session.id)
        }
    }

    private fun getUsernameFromSession(session: WebSocketSession): String? {
        val uri = session.handshakeInfo.uri.toString()
        val uriComponents = UriComponentsBuilder.fromUriString(uri).build()
        return uriComponents.queryParams.getFirst("username")
    }

    private fun handleMessage(messageText: String, session: WebSocketSession): Mono<Void> {
        return try {
            val message = objectMapper.readValue(messageText, ChatMessage::class.java)
            when (message.command) {
                "join", "create" -> handleJoinOrCreateChatRoom(message, session)
                "send" -> broadcastMessage(message)
                else -> Mono.empty()
            }
        } catch (e: Exception) {
            println("Error parsing message: $messageText")
            Mono.empty<Void>()
        }
    }

    private fun handleJoinOrCreateChatRoom(message: ChatMessage, session: WebSocketSession): Mono<Void> {
        sessionManager.addUserToChatRoom(message.sender, message.chatRoomId)
        return Mono.empty<Void>()
    }

    // TODO: blocking 되는 부분 수정 필요
    private fun broadcastMessage(message: ChatMessage): Mono<Void> {
        val sessions = sessionManager.getSessionsForChatRoom(message.chatRoomId)
        val broadcast = Flux.fromIterable(sessions)
            .flatMap { sessionToSend ->
                if (sessionToSend != null) {
                    sessionToSend.send(Mono.just(sessionToSend.textMessage(objectMapper.writeValueAsString(message))))
                } else {
                    println("메세지를 받을 세션이 존재하지 않습니다.")
                    Mono.empty<Void>()
                }
            }.subscribe()

        return Mono.empty()
    }
}

data class ChatMessage(
    val command: String,
    val content: String,
    val sender: String,
    val chatRoomId: String,
    val participants: Set<String>,
)
