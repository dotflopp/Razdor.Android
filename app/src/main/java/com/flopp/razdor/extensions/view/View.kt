package com.flopp.razdor.extensions.view

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout


inline fun View.boundsLinear(
    gravity: Int = Gravity.START,
    top: Float = 0f,
    start: Float = 0f,
    bottom: Float = 0f,
    end: Float = 0f,
    width: Float = -2f,
    height: Float = -2f
) {
    layoutParams = LinearLayout.LayoutParams(
        width.toInt(),
        height.toInt()
    ).apply {
        topMargin = top.toInt()
        leftMargin = start.toInt()
        rightMargin = end.toInt()
        bottomMargin = bottom.toInt()
        this.gravity = gravity
    }
}

inline fun View.boundsFrame(
    gravity: Int = Gravity.START,
    top: Float = 0f,
    start: Float = 0f,
    bottom: Float = 0f,
    end: Float = 0f,
    width: Float = -2f,
    height: Float = -2f
) {
    layoutParams = FrameLayout.LayoutParams(
        width.toInt(),
        height.toInt()
    ).apply {
        topMargin = top.toInt()
        leftMargin = start.toInt()
        rightMargin = end.toInt()
        bottomMargin = bottom.toInt()
        this.gravity = gravity
    }
}