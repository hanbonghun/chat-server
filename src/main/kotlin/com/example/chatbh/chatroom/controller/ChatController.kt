package com.example.chatbh.chatroom.controller

import com.example.chatbh.chatroom.ChatRoom
import com.example.chatbh.chatroom.service.ChatRoomService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/chatrooms")
class ChatController(private val chatRoomService: ChatRoomService) {

    data class ChatRoomRequest(val name: String ,val participants: Set<String>)

    @PostMapping
    fun createChatRoom(@RequestBody request: ChatRoomRequest): Mono<ResponseEntity<ChatRoom>> {
        return chatRoomService.createChatRoom(request.name, request.participants)
            .map { chatRoom -> ResponseEntity.ok(chatRoom) }
    }

    @GetMapping
    fun getMyChatRooms(@RequestParam username: String): Mono<ResponseEntity<List<ChatRoom>>> {
        return chatRoomService.getMyChatRooms(username)
            .map { chatRooms -> ResponseEntity.ok(chatRooms) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping("/{chatRoomId}")
    fun getChatRoom(@PathVariable chatRoomId: String): Mono<ResponseEntity<ChatRoom>> {
        return chatRoomService.getChatRoomById(chatRoomId)
            .map { chatRoom -> ResponseEntity.ok(chatRoom) }
    }

}