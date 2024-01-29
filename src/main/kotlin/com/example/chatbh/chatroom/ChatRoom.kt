package com.example.chatbh.chatroom

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class ChatRoom(
    @Id
    val id: String, // 채팅방의 고유 ID
    val name: String,
    val participants: Set<String>, // 참여자 username 목록
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)