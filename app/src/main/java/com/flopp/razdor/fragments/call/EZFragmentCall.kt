package com.flopp.razdor.fragments.call

import android.content.Context
import android.view.Gravity
import com.flopp.razdor.EZApp
import com.flopp.razdor.bridges.EZBridgeRtcService
import com.flopp.razdor.extensions.mainActivity
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.network.rtc.clients.EZClientWebRtc
import com.flopp.razdor.views.surface.EZViewSurfaceRtc
import good.damn.ui.layouts.UIFrameLayout

class EZFragmentCall
: EZFragmentNavigation() {

    private var mBridgeRtcService: EZBridgeRtcService? = null
    private var mClientRtc: EZClientWebRtc? = null

    override fun onStart() {
        super.onStart()
        context?.apply {
            mBridgeRtcService = EZBridgeRtcService(
                mainActivity()
            )
        }
    }

    override fun onStop() {
        super.onStop()
        context?.apply {
            mBridgeRtcService?.stop(
                mainActivity()
            )
        }
    }

    override fun onCreateView(
        context: Context
    ) = UIFrameLayout(
        context
    ).apply {
        applyTheme(
            EZApp.theme
        )

        EZViewSurfaceRtc(
            context
        ).apply {

            setBackgroundColor(
                0xff08193A.toInt()
            )

            addView(
                this
            )
        }

        EZViewSurfaceRtc(
            context
        ).apply {

            setBackgroundColor(
                0xffff0000.toInt()
            )

            boundsFrame(
                gravity = Gravity.END or Gravity.BOTTOM,
                width = EZApp.width * 0.1f,
                height = EZApp.height * 0.1f
            )

            addView(
                this
            )
        }
    }

}