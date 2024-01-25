package com.example.chatbh.message

data class Message(
    val id: String, // 메시지의 고유 ID
    val chatRoomId: String, // 메시지가 속한 채팅방 ID
    val senderId: String, // 메시지 발신자의 ID
    val content: String, // 메시지 내용
    val timestamp: Long // 메시지 발신 시간
)
