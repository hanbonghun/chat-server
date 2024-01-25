package com.example.chatbh.chatroom

data class ChatRoom(
    val id: String, // 채팅방의 고유 ID
    val participants: Set<String> // 참여자의 ID 목록
)
