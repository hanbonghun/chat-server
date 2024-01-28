//package com.example.chatbh
//
//import com.example.chatbh.websocket.service.WebSocketSessionManager
//import org.springframework.data.redis.core.ReactiveRedisTemplate
//import org.springframework.data.redis.listener.ChannelTopic
//import org.springframework.stereotype.Service
//import reactor.core.Disposable
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import java.util.concurrent.ConcurrentHashMap
//
//@Service
//class RedisMessageSubscriber(
//    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
//    private val sessionManager: WebSocketSessionManager,
//) {
//
//    private val topicSubscriptions = ConcurrentHashMap<String, Disposable>()
//
//    fun subscribeToChatRoom(chatRoomId: String) {
//        val topic = ChannelTopic("chatRoom:$chatRoomId")
//        val subscription = reactiveRedisTemplate.listenTo(topic)
//            .map { it.message }
//            .subscribe { message ->
//                println("gdgd")
//                // 채팅방에 속한 모든 세션에 메시지 전송
//                sessionManager.getSessionsForChatRoom(chatRoomId).forEach { sessionId ->
//                    sendMessageToSession(sessionId, message)
//                }
//            }
//
//        topicSubscriptions[chatRoomId] = subscription
//    }
//
//    private fun sendMessageToSession(sessionId: String, message: String) {
//        val session = sessionManager.getSessionById(sessionId)
//        println("sessionzxzxzx : ${session}")
//        println("message ${message}")
//        session?.send(Mono.just(session.textMessage(message)))?.subscribe()
//    }
//
//    fun getMessages(chatRoomId: String): Flux<String> {
//        val topic = ChannelTopic("chatRoom:$chatRoomId")
//        return reactiveRedisTemplate.listenTo(topic).map { it.message }
//    }
//}