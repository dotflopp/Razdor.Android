package com.flopp.razdor.services.rtc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue

class EZServiceRtc
: Service() {

    companion object {
        private val TAG = EZServiceRtc::class.simpleName
    }

    private var mBinder: EZIBinderRtc? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d(TAG, "onStartCommand: $flags $startId")
        // https://developer.android.com/develop/background-work/services#ExtendingService
        return START_NOT_STICKY
    }

    override fun onUnbind(
        intent: Intent?
    ): Boolean {
        Log.d(TAG, "onUnbind: $mBinder")
        mBinder = null
        return true // allow rebind
    }

    override fun onRebind(
        intent: Intent?
    ) = Unit

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        Log.d(TAG, "onBind: $mBinder")
        if (mBinder == null) {
            mBinder = EZIBinderRtc().apply {
                service = this@EZServiceRtc
            }
        }
        return mBinder
    }


}