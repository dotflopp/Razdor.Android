package com.example.zov_android.extensions.json

import com.example.zov_android.model.EZModelUser
import org.json.JSONObject

fun JSONObject.toUser() = EZModelUser(
    id = getString("id"),
    token = getString("token"),
    username = getString("username")
)