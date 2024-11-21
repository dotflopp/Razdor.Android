package com.example.zov_android

import android.app.Application
import java.net.URL

class EZApp
: Application() {

    companion object {
        var width = 0f
        var height = 0f

        const val rootUrl = "https://github.com"
    }

    override fun onCreate() {
        super.onCreate()

        resources.displayMetrics.apply {
            width = widthPixels.toFloat()
            height = heightPixels.toFloat()
        }

    }


}