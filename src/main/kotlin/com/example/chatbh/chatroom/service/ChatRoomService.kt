package com.example.chatbh.chatroom.service

import com.example.chatbh.chatroom.ChatRoom
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatRoomService {
    private val chatRooms: MutableMap<String, ChatRoom> = ConcurrentHashMap()

    fun createOrGetChatRoom(participants: Set<String>): ChatRoom {
        val chatRoomId = if (participants.size == 2) {
            // 1:1 채팅방 ID 생성 로직
            generateOneToOneChatRoomId(participants)
        } else {
            // 단체 채팅방의 경우, 고유한 ID 생성
            UUID.randomUUID().toString()
        }

        println("chatRoomId : ${chatRoomId}" )

        return chatRooms.getOrPut(chatRoomId) {
            ChatRoom(chatRoomId, participants)
        }
    }

    private fun generateOneToOneChatRoomId(participants: Set<String>): String {
        // 알파벳 순서대로 정렬하여 ID 생성
        return participants.sorted().joinToString("_")
    }

    fun findChatRoom(chatRoomId: String): ChatRoom? {
        return chatRooms[chatRoomId]
    }
}