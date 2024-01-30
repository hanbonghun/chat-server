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

    // 채팅방 리스너를 동적으로 추가하는 메소드
    fun addChatRoomListener(chatRoomId: String) {
        val channelTopic = ChannelTopic("chatRoom:$chatRoomId")
        messageListenerContainer.addMessageListener(this, channelTopic)
        println("Listener added for chatRoom:$chatRoomId")
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        println("메세지왓음")
        val channel = String(message.channel)
        val chatRoomId = channel.split(":")[1]
        val payload = String(message.body)
        println("payload ${payload}")

        chatRoomSessionService.getSessionsInChatRoom(chatRoomId)?.forEach { sessionId ->
            print("보낼 세션 ${sessionId}")
            val session = sessionManager.getSessionById(sessionId)
            session?.let {
                if (it.isOpen) {
                    it.send(Mono.just(it.textMessage(payload))).subscribe()
                }
            }
        }
    }

    fun createChatRoomChannel(chatRoomId: String): Mono<Void> {
        val channelName = "chatRoom:$chatRoomId"
        redisTemplate.opsForValue().set(channelName, "")
        println("channelName : ${channelName} 등록 완료")
        return Mono.empty()
    }

    fun publishToChatRoomChannel(chatRoomId: String, message: String) {
        println("publish channel : chatRoom:${chatRoomId}")
        redisTemplate.convertAndSend("chatRoom:$chatRoomId", message)
    }
}