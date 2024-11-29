package com.flopp.razdor.services.rtc

import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import java.io.FileDescriptor

class EZIBinderRtc
: IBinder {

    var service: EZServiceRtc? = null

    override fun getInterfaceDescriptor(): String? {
        return null
    }

    override fun pingBinder(): Boolean {
        return false
    }

    override fun isBinderAlive(): Boolean {
        return false
    }

    override fun queryLocalInterface(
        descriptor: String
    ): IInterface? {
        return null
    }

    override fun dump(
        fd: FileDescriptor,
        args: Array<out String>?
    ) {

    }

    override fun dumpAsync(
        fd: FileDescriptor,
        args: Array<out String>?
    ) {

    }

    override fun transact(
        code: Int,
        data: Parcel,
        reply: Parcel?,
        flags: Int
    ): Boolean {
        return false
    }

    override fun linkToDeath(
        recipient: IBinder.DeathRecipient,
        flags: Int
    ) {

    }

    override fun unlinkToDeath(
        recipient: IBinder.DeathRecipient,
        flags: Int
    ): Boolean {
        return false
    }


}