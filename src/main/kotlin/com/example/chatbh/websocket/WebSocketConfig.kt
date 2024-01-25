package com.example.chatbh.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy

@Configuration
class WebSocketConfig(private val reactiveWebSocketHandler: ReactiveWebSocketHandler) {

    @Bean
    fun handlerMapping(): HandlerMapping {
        val map = mapOf("/ws/chat" to reactiveWebSocketHandler)
        return SimpleUrlHandlerMapping(map, 1)
    }

    @Bean
    fun handlerAdapter() = WebSocketHandlerAdapter(webSocketService())

    @Bean
    fun webSocketService(): WebSocketService {
        val strategy = ReactorNettyRequestUpgradeStrategy()
        return HandshakeWebSocketService(strategy)
    }
}