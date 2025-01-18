package com.flopp.razdor.fragments.call

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import com.flopp.razdor.EZApp
import com.flopp.razdor.bridges.EZBridgeRtcService
import com.flopp.razdor.extensions.mainActivity
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.model.EZModelUser
import com.flopp.razdor.network.rtc.clients.EZClientWebRtc
import com.flopp.razdor.views.surface.EZViewSurfaceRtc
import good.damn.ui.buttons.UIButton
import good.damn.ui.layouts.UIFrameLayout

class EZFragmentCall
: EZFragmentNavigation() {

    var fromUser: EZModelUser? = null

    private var mBridgeRtcService: EZBridgeRtcService? = null

    private var mSurfaceRemote: EZViewSurfaceRtc? = null
    private var mSurfaceLocal: EZViewSurfaceRtc? = null

    override fun onStart() {
        super.onStart()

        val context = context
            ?: return

        val fromUser = fromUser
            ?: return

        mBridgeRtcService = EZBridgeRtcService(
            context.mainActivity(),
            fromUser
        )
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

            mSurfaceRemote = this
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

            mSurfaceLocal = this
        }

        val s = EZApp.height * 0.1f

        Button(
            context
        ).apply {

            text = "CALL"

            setOnClickListener {
                var selectedUser: EZModelUser? = null
                for (user in EZApp.testUsers) {
                    if (fromUser?.username != user.username) {
                        selectedUser = user
                        break
                    }
                }

                Log.d("EZFragmentCall:", "onCreateView: CALL: $selectedUser")

                selectedUser?.apply {
                    mBridgeRtcService?.callTo(
                        this
                    )
                }
            }

            boundsFrame(
                width = s,
                height = s
            )

            addView(
                this
            )
        }


        Button(
            context
        ).apply {

            text = "RCV"

            setOnClickListener {
                var selectedUser: EZModelUser? = null
                for (user in EZApp.testUsers) {
                    if (fromUser?.username != user.username) {
                        selectedUser = user
                        break
                    }
                }

                Log.d("EZFragmentCall:", "onCreateView: ANSWER: $selectedUser")
                selectedUser?.apply {
                    mBridgeRtcService?.answerTo(
                        this
                    )
                }
            }

            boundsFrame(
                width = s,
                height = s,
                start = s
            )

            addView(
                this
            )
        }
    }

}