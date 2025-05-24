package com.example.musicapp

data class Comment(
    val songId: Int,
    val user: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)