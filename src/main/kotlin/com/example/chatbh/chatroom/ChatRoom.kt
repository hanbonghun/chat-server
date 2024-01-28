package com.example.chatbh.chatroom

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ChatRoom(
    @Id
    val id: String, // 채팅방의 고유 ID
    val participants: Set<String> // 참여자 username 목록
)