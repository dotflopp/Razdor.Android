package com.flopp.razdor

import android.app.Application
import com.flopp.razdor.services.EZServicesSystem
import com.flopp.razdor.themes.UIThemeDark
import good.damn.ui.theme.UITheme

class EZApp
: Application() {

    companion object {

        var services: EZServicesSystem? = null

        var width = 0f
        var height = 0f
        var insetTop = 0f
        var insetBottom = 0f

        var theme: UITheme = UIThemeDark()

        const val rootUrl = "https://github.com"
    }

    override fun onCreate() {
        super.onCreate()

        services = EZServicesSystem(
            applicationContext
        )

        resources.displayMetrics.apply {
            width = widthPixels.toFloat()
            height = heightPixels.toFloat()
        }

    }

    override fun onTerminate() {
        services = null
        super.onTerminate()
    }


}