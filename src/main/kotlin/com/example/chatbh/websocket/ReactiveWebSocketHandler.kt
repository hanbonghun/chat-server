package com.example.chatbh.websocket

import com.example.chatbh.ChatRoomChannelService
import com.example.chatbh.chatroom.service.ChatRoomService
import com.example.chatbh.chatroom.service.ChatRoomSessionService
import com.example.chatbh.user.service.UserSessionService
import com.example.chatbh.websocket.service.WebSocketSessionManager
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ReactiveWebSocketHandler(
    private val sessionManager: WebSocketSessionManager,
    private val chatRoomService: ChatRoomService,
    private val chatRoomChannelService: ChatRoomChannelService,
    private val userSessionService: UserSessionService,
    private val chatRoomSessionService: ChatRoomSessionService,
) : WebSocketHandler {
    private val objectMapper = jacksonObjectMapper()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val userId = sessionManager.getUserIdFromSession(session)
        println("Connected userId: $userId")

        // 세션 등록
        userId?.let {
            sessionManager.registerSession(session.id, it, session)
            userSessionService.mapSessionToUser(session.id, userId)
        }

        val outputMessages = session.receive()
            .map { msg -> handleMessage(msg.payloadAsText, session) }
            .then()

        return outputMessages.doFinally {
            println("세션 해제 ")
            sessionManager.removeSession(session.id)
        }
    }

    private fun handleMessage(messageText: String, session: WebSocketSession): Mono<Void> {
        return try {
            val request = objectMapper.readValue(messageText, WebSocketRequest::class.java)
            when (request.type) {
                "create" -> {
                    val payload =
                        objectMapper.treeToValue(request.payload, ChatRoomCreatePayload::class.java)
                    handleCreateChatRoom(payload, session)
                }
                "join" -> {
                    val payload =
                        objectMapper.treeToValue(request.payload, ChatRoomJoinPayload::class.java)
                    handleJoinChatRoom(payload, session)
                }
                "send" -> {
                    val payload = objectMapper.treeToValue(request.payload, ChatRoomMessagePayload::class.java)
                    handleSendMessage(payload, session)
                }
                else -> Mono.empty()
            }
        } catch (e: Exception) {
            println("Error parsing message: $messageText, Exception: ${e.message}")
            e.printStackTrace()
            Mono.error(e)
        }
    }

    private fun handleCreateChatRoom(
        payload: ChatRoomCreatePayload,
        session: WebSocketSession
    ): Mono<Void> {
        chatRoomService.createChatRoom(payload.name, payload.userIds.toSet())
            .flatMap { chatRoom ->
                chatRoomChannelService.createChatRoomChannel(chatRoom.id)
            }
            .onErrorResume { e ->
                println("채팅방 생성 중 에러 발생: ${e.message}")
                Mono.empty()
            }
            .subscribe()

        return Mono.empty()

    }

    private fun handleJoinChatRoom(
        payload: ChatRoomJoinPayload,
        session: WebSocketSession
    ): Mono<Void> {
        val chatRoomId = payload.chatRoomId
        val userId = payload.userId

        // 세션을 채팅방에 매핑
        chatRoomSessionService.addSessionToChatRoom(session.id, chatRoomId)
        userSessionService.mapSessionToUser(session.id, userId)

        return Mono.empty()
    }

    private fun handleSendMessage(
        payload: ChatRoomMessagePayload,
        session: WebSocketSession
    ): Mono<Void> {
        val messageJson = objectMapper.writeValueAsString(payload)
        chatRoomChannelService.publishToChatRoomChannel(payload.chatRoomId, messageJson)

        return Mono.empty()
    }
}

data class WebSocketRequest(
    val type: String,
    val payload: JsonNode
)

data class ChatRoomCreatePayload(
    val name: String,
    val userIds: Set<String>
)

data class ChatRoomJoinPayload(
    val chatRoomId: String,
    val userId: String
)

data class ChatRoomMessagePayload(
    val chatRoomId: String,
    val senderId: String,
    val message: String
)