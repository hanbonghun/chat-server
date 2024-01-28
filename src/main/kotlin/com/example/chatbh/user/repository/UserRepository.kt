package com.example.chatbh.user.repository

import com.example.chatbh.user.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
interface UserRepository : ReactiveMongoRepository<User, String>