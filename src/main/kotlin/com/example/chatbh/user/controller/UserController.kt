package com.example.chatbh.user.controller

import com.example.chatbh.user.User
import com.example.chatbh.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    data class UserRequest(
        val name: String,
        val email: String,
    )

    @PostMapping
    fun createUser(@RequestBody request: UserRequest): Mono<ResponseEntity<User>> {
        return userService.createUser(request)
            .map { user -> ResponseEntity.ok(user) }
    }

    @GetMapping
    fun getAllUsers(): Mono<ResponseEntity<List<User>>> {
        return userService.getUsers()
            .collectList()
            .map { users -> ResponseEntity.ok(users) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }
}