package com.example.chatbh.chatroom.controller

import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.service.ChatRoomService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/chats")
class ChatController(private val chatRoomService: ChatRoomService) {

    data class ChatRoomRequest(val participants: Set<String>)

    @PostMapping
    fun createOrGetChatRoom(@RequestBody request:ChatRoomRequest): Mono<ResponseEntity<ChatRoom>> {
        return chatRoomService.createOrGetChatRoom(request.participants)
            .map { chatRoom -> ResponseEntity.ok(chatRoom) }
    }

    @GetMapping("/{chatRoomId}")
    fun getChatRoom(@PathVariable chatRoomId: String): Mono<ResponseEntity<ChatRoom>> {
        return chatRoomService.findChatRoom(chatRoomId)
            .map { chatRoom -> ResponseEntity.ok(chatRoom) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }
}