package com.flopp.razdor

import android.app.Application
import com.flopp.razdor.themes.UIThemeDark
import good.damn.ui.theme.UITheme

class EZApp
: Application() {

    companion object {
        var width = 0f
        var height = 0f
        var insetTop = 0f
        var insetBottom = 0f

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