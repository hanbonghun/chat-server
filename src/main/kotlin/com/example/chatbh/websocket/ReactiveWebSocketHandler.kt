package com.example.chatbh.websocket

import com.example.chatbh.RedisMessagePublisher
import com.example.chatbh.RedisMessageSubscriber
import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.service.ChatRoomService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class ReactiveWebSocketHandler(
    private val messagePublisher: RedisMessagePublisher,
    private val messageSubscriber: RedisMessageSubscriber,
    private val chatRoomService: ChatRoomService
) : WebSocketHandler {

    private val objectMapper = jacksonObjectMapper()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val inputMessages = session.receive()
            .map { it.payloadAsText }
            .flatMap { message ->
                val messageData = parseMessage(message)
                when (messageData.command) {
                    "create" -> handleCreateChatRoomCommand(messageData)
                    "send" -> handleSendMessageCommand(messageData)
                    else -> Mono.error(IllegalArgumentException("Unknown command"))
                }
            }
            .then()

        val outputMessages = messageSubscriber.getMessages()
            .map { session.textMessage(it) }

        return session.send(outputMessages).and(inputMessages)
    }

    private fun handleMessage(message: String, chatRoom: ChatRoom) {
        // 메시지를 해당 채팅방의 모든 참가자에게 전송
        chatRoom.participants.forEach { userId ->
            println("userId :  ${userId}")
            // Redis를 통해 메시지 발송
            messagePublisher.publish(objectMapper.writeValueAsString(message))
        }
    }

    private fun parseMessage(message: String): ChatMessage {
        return objectMapper.readValue(message)
    }

    private fun handleCreateChatRoomCommand(message: ChatMessage): Mono<Void> {
        // 채팅방 생성을 위한 참가자 목록을 추출
        val participants = message.participants + message.sender // 메시지 발신자를 포함합니다.

        // 채팅방을 생성하거나 가져옴
        chatRoomService.createOrGetChatRoom(participants)

        return Mono.empty()
    }

    private fun handleSendMessageCommand(message: ChatMessage): Mono<Void> {
        val chatRoom = chatRoomService.findChatRoom(message.chatRoomId)
        return if (chatRoom != null) {
            handleMessage(message.content, chatRoom)
            Mono.empty()
        } else {
            Mono.error(IllegalStateException("Chat room not found"))
        }
    }
}

data class ChatMessage(
    val command: String, // "create" 또는 "send"
    val type: String,    // 예: "chat"
    val content: String,
    val sender: String,
    val chatRoomId: String,
    val participants: Set<String> // 채팅방에 참여할 사용자들의 목록
)