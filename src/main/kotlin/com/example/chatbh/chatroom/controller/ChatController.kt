package com.example.chatbh.chatroom.controller

import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.service.ChatRoomService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chats")
class ChatController(private val chatRoomService: ChatRoomService) {

    @PostMapping
    fun createOrGetChatRoom(@RequestBody participants: Set<String>): ResponseEntity<ChatRoom> {
        val chatRoom = chatRoomService.createOrGetChatRoom(participants)
        return ResponseEntity.ok(chatRoom)
    }

    @GetMapping("/{chatRoomId}")
    fun getChatRoom(@PathVariable chatRoomId: String): ResponseEntity<ChatRoom> {
        val chatRoom = chatRoomService.findChatRoom(chatRoomId)
        return if (chatRoom != null) {
            ResponseEntity.ok(chatRoom)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}