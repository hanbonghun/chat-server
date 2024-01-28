package com.example.chatbh.chatroom.repository

import com.example.chatbh.chatroom.ChatRoom
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ChatRoomRepository : ReactiveMongoRepository<ChatRoom, String> {
    fun findByParticipantsContains(username: String): Flux<ChatRoom>
}