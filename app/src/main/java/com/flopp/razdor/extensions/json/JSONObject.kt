package com.flopp.razdor.extensions.json

import com.flopp.razdor.model.EZModelUser
import org.json.JSONObject

fun JSONObject.toUser() = EZModelUser(
    id = getString("id"),
    token = getString("token"),
    username = getString("username")
)