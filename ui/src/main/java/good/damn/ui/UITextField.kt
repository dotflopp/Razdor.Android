package good.damn.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import good.damn.ui.theme.UITheme

class UITextField(
    context: Context
): UIView(
    context
) {

    companion object {
        private val TAG = UITextField::class.simpleName
    }

    private val mPaintStroke = Paint().apply {
        style = Paint.Style.STROKE
    }

    init {
        background = null
        animationTouchDown = null
        animationTouchUp = null
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {

    }

    override fun applyTheme(
        theme: UITheme
    ) {

    }

    override fun onTouchEvent(
        event: MotionEvent?
    ): Boolean {

        if (event == null) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_UP -> {

            }
        }

        return true
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?
    ): Boolean {

        Log.d(TAG, "onKeyDown ${event?.action} $keyCode->${keyCode.toChar()}")
        
        return super.onKeyDown(
            keyCode,
            event
        )
    }

}