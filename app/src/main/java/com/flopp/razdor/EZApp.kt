package com.flopp.razdor

import android.app.Application
import com.flopp.razdor.model.EZModelUser
import com.flopp.razdor.patterns.EZPatternEmail
import com.flopp.razdor.patterns.EZPatternPassword
import com.flopp.razdor.services.EZServicesSystem
import com.flopp.razdor.themes.UIThemeDark
import good.damn.ui.theme.UITheme
import org.webrtc.EglBase
import java.util.regex.Pattern

class EZApp
: Application() {

    companion object {

        var services: EZServicesSystem? = null

        var width = 0f
        var height = 0f
        var insetTop = 0f
        var insetBottom = 0f

        var theme: UITheme = UIThemeDark()

        val patternPassword = EZPatternPassword()
        val patternEmail = EZPatternEmail()

        val eglBaseContext = EglBase.create().eglBaseContext

        val testUsers = arrayOf(
            EZModelUser(
                "0f0d0s-asdaslsk",
                "mouse",
                "mouse@gmail.com"
            ),
            EZModelUser(
                "0f0a0s-assadgkfmslsk",
                "gooddamn",
                "gooddamn@gmail.com"
            )
        )

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