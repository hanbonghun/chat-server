package com.example.chatbh.chatroom.service

import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.repository.ChatRoomRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
) {
    fun createChatRoom(name: String, participants: Set<String>): Mono<ChatRoom> {
        val chatRoomId = if (participants.size == 2) {
            generateOneToOneChatRoomId(participants)
        } else {
            UUID.randomUUID().toString()
        }

        val now = LocalDateTime.now() // 현재 시간 가져오기

        val chatRoom = ChatRoom(
            id = chatRoomId,
            name = name,
            participants = participants,
            createdAt = now,
            updatedAt = now
        )
        return chatRoomRepository.save(chatRoom).toMono()
    }

    fun generateOneToOneChatRoomId(participants: Set<String>): String {
        return participants.sorted().joinToString("_")
    }

    fun getChatRoomById(chatRoomId: String): Mono<ChatRoom> {
        return chatRoomRepository.findById(chatRoomId)
    }

    // 내가 참여한 채팅방 목록 조회
    fun getMyChatRooms(username: String): Mono<List<ChatRoom>> {
        return chatRoomRepository.findByParticipantsContains(username).collectList()
    }
}