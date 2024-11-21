package com.example.zov_android.extensions.okhttp

import okhttp3.ResponseBody
import org.json.JSONObject

fun ResponseBody.toJSON() =
    JSONObject(string())