package com.example.chatbh.user.service

import com.example.chatbh.user.User
import com.example.chatbh.user.controller.UserController
import com.example.chatbh.user.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(userRequest: UserController.UserRequest): Mono<User> {
        val user = User(
            UUID.randomUUID().toString(),
            userRequest.name,
            userRequest.email
        )
        return userRepository.save(user).toMono()
    }

    fun getUsers(): Flux<User> {
        return userRepository.findAll();
    }

    fun getUser(id: String): Mono<User> {
        return userRepository.findById(id)
    }
}