package com.example.chatbh.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    @Id
    val id: String, // 고유한 사용자 식별자
    val name: String, // 사용자 이름
    val email: String, // 로그인 시 사용되는 사용자 이메일
)
