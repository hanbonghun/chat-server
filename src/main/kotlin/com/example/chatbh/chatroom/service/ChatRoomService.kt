package com.example.chatbh.chatroom.service

import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.repository.ChatRoomRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.UUID

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
) {
    fun createChatRoom(participants: Set<String>): Mono<ChatRoom> {
        val chatRoomId = if (participants.size == 2) {
            generateOneToOneChatRoomId(participants)
        } else {
            UUID.randomUUID().toString()
        }

        val chatRoom = ChatRoom(chatRoomId, participants)
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