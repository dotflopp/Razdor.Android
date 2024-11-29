package com.flopp.razdor.bridges

import android.content.ComponentName
import android.content.Context
import android.content.Context.BindServiceFlags
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.flopp.razdor.services.rtc.EZIBinderRtc
import com.flopp.razdor.services.rtc.EZServiceRtc

class EZBridgeRtcService(
    activity: AppCompatActivity
) {

    private val mServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            mService = (
                service as? EZIBinderRtc
            )?.service
        }

        override fun onServiceDisconnected(
            name: ComponentName?
        ) {
            mService = null
        }
    }

    private var mService: EZServiceRtc? = null

    init {
        activity.apply {
            bindService(
                Intent(
                    this,
                    EZServiceRtc::class.java
                ),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }


    fun stop(
        activity: AppCompatActivity
    ) {
        activity.unbindService(
            mServiceConnection
        )
        activity.stopService(
            Intent(
                activity,
                EZServiceRtc::class.java
            )
        )
    }

}