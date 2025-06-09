package com.example.zov_android.data.models.response

data class Attachment(
    val id: String,
    val fileName: String,
    val sourceUrl: String,
    val mediaType: String,
    val size: Int
)