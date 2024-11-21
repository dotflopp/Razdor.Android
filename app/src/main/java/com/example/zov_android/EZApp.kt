package com.example.zov_android

import android.app.Application
import com.example.zov_android.themes.UIThemeDark
import good.damn.ui.theme.UITheme

class EZApp
: Application() {

    companion object {
        var width = 0f
        var height = 0f
        var theme: UITheme = UIThemeDark()
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