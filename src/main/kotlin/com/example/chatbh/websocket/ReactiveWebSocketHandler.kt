package com.example.chatbh.websocket

import com.example.chatbh.RedisMessagePublisher
import com.example.chatbh.RedisMessageSubscriber
import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.service.ChatRoomService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
                if (messageData.command == "send") {
                    handleSendMessageCommand(messageData)
                } else {
                    Mono.error(IllegalArgumentException("Unknown or unsupported command"))
                }
            }
            .then()

        val outputMessages = messageSubscriber.getMessages()
            .map { session.textMessage(it) }

        return session.send(outputMessages).and(inputMessages)
    }

    private fun handleMessage(message: String, chatRoom: ChatRoom) {
        chatRoom.participants.forEach { userId ->
            // 메시지를 Redis를 통해 해당 채팅방의 모든 참가자에게 발행
            messagePublisher.publish(objectMapper.writeValueAsString(message))
        }
    }

    private fun parseMessage(message: String): ChatMessage {
        return objectMapper.readValue(message,  ChatMessage::class.java)
    }

    private fun handleSendMessageCommand(message: ChatMessage): Mono<Void> {
        return chatRoomService.findChatRoom(message.chatRoomId)
            .flatMap { chatRoom ->
                handleMessage(message.content, chatRoom)
                Mono.empty<Void>()
            }
            .switchIfEmpty(Mono.error(IllegalStateException("Chat room not found")))
    }
}

data class ChatMessage(
    val command: String,
    val content: String,
    val sender: String,
    val chatRoomId: String,
    val participants: Set<String> // 채팅방에 참여할 사용자들의 목록
)
