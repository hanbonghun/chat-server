package com.example.chatbh

import com.example.chatbh.chatroom.service.ChatRoomSessionService
import com.example.chatbh.websocket.service.WebSocketSessionManager
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ChatRoomChannelService(
    private val redisTemplate: StringRedisTemplate,
    private val chatRoomSessionService: ChatRoomSessionService,
    private val sessionManager: WebSocketSessionManager,
    private val messageListenerContainer: RedisMessageListenerContainer  // 여기에 주입
) : MessageListener {

    init {
        messageListenerContainer.addMessageListener(this, ChannelTopic("chatRoom:*"))
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val channel = String(message.channel)
        val chatRoomId = channel.split(":")[1]
        val payload = String(message.body)

        chatRoomSessionService.getSessionsInChatRoom(chatRoomId)?.forEach { sessionId ->
            val session = sessionManager.getSessionById(sessionId)
            session?.let {
                if (it.isOpen) {
                    it.send(Mono.just(it.textMessage(payload))).subscribe()
                }
            }
        }
    }

    fun createChatRoomChannel(chatRoomId: String): Mono<Void> {
        val channelName = "chatroom:$chatRoomId"
        redisTemplate.opsForValue().set(channelName, "")
        println("channelName : ${channelName} 등록 완료")
        return Mono.empty()
    }

    fun publishToChatRoomChannel(chatRoomId: String, message: String) {
        redisTemplate.convertAndSend("chatRoom:$chatRoomId", message)
    }
}