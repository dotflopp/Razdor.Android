package com.flopp.razdor.extensions.okhttp

import okhttp3.ResponseBody
import org.json.JSONObject

fun ResponseBody.toJSON() =
    JSONObject(string())