package com.example.zov_android.data.utils

import android.util.Base64
import java.nio.ByteBuffer

data class TokenData(
    val userId: Long,
    val creationTime: Long,
    val signature: String
)

fun decodeToken(token: String): TokenData {
    // Разделяем токен на части
    val parts = token.split(".")
    require(parts.size == 3) { "Invalid token format" }

    // Декодируем UserId (Snowflake)
    val userIdBytes = Base64.decode(parts[0], Base64.URL_SAFE or Base64.NO_PADDING)
    val userId = ByteBuffer.wrap(userIdBytes).long

    // Декодируем CreationTime
    val creationTimeBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING)
    val creationTime = ByteBuffer.wrap(creationTimeBytes).long

    return TokenData(
        userId = userId,
        creationTime = creationTime,
        signature = parts[2]
    )
}

fun parseSnowflake(snowflake: Long): Triple<Long, Int, Int> {
    val timestamp = snowflake shr 22           // 42 бита
    val workerId = ((snowflake shr 18) and 0x0F).toInt()  // 4 бита (сдвиг на 18, маска 0x0F)
    val sequence = (snowflake and 0x3FFFF).toInt()        // 18 бит (маска 0x3FFFF)

    return Triple(timestamp, workerId, sequence)
}
