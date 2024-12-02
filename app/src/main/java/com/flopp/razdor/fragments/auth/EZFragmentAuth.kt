package com.flopp.razdor.fragments.auth

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.adapters.EZAdapterPager
import com.flopp.razdor.extensions.checkPermissionCamera
import com.flopp.razdor.extensions.checkPermissionMicrophone
import com.flopp.razdor.extensions.mainActivity
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.fragments.auth.interfaces.EZListenerOnLoginSuccess
import com.flopp.razdor.fragments.auth.interfaces.EZListenerOnSignInSuccess
import com.flopp.razdor.fragments.call.EZFragmentCall
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.model.EZModelUser
import good.damn.ui.UIViewShaper
import good.damn.ui.components.shapes.UICanvasCircle
import good.damn.ui.components.shapes.animation.data.UICanvasShapeAnimationCircle
import good.damn.ui.layouts.UIFrameLayout
import com.flopp.razdor.pagers.EZViewPagerShaper
import good.damn.editor.importer.VEModelImport
import good.damn.editor.importer.VEViewAVS
import good.damn.sav.misc.Size

class EZFragmentAuth
: EZFragmentNavigation(), EZListenerOnLoginSuccess, EZListenerOnSignInSuccess {

    companion object {
        private val TAG = EZFragmentAuth::class.simpleName
    }

    private var mViewPagerShaper: EZViewPagerShaper? = null

    private var w = EZApp.width
    private var h = EZApp.height

    private val mShapeAnimationLogin: Array<Pair<Any,Any>> = arrayOf(
        Pair(
            UICanvasShapeAnimationCircle(
                x = 0.0f,
                y = h * 0.25f,
                radius = w * 0.2f
            ),
            UICanvasShapeAnimationCircle(
                x = w * 0.25f,
                y = h * 0.9f,
                radius = w * 0.4f
            )
        ),
        Pair(
            UICanvasShapeAnimationCircle(
                x = w * 0.87f,
                y = h * 0.8f,
                radius = w * 0.1f
            ),
            UICanvasShapeAnimationCircle(
                x = w * 0.25f,
                y = h * 0.25f,
                radius = w * 0.25f
            )
        ),
        Pair(
            UICanvasShapeAnimationCircle(
                x = w * 0.67f,
                y = h * 0.2f,
                radius = w * 0.4f
            ),
            UICanvasShapeAnimationCircle(
                x = w * 0.85f,
                y = h * 0.5f,
                radius = w * 0.12f
            )
        )
    )

    override fun onCreateView(
        context: Context
    ) = UIFrameLayout(
        context
    ).let { root ->

        context.mainActivity().apply {
            checkPermissionCamera()
            checkPermissionMicrophone()
        }

        root.applyTheme(
            EZApp.theme
        )

        val shaper = UIViewShaper(
            context
        ).apply {
            shapes = arrayOf(
                UICanvasCircle(
                    x = 0.0f,
                    y = h * 0.25f,
                    radius = w * 0.2f
                ),
                UICanvasCircle(
                    x = w * 0.87f,
                    y = h * 0.8f,
                    radius = w * 0.1f
                ),
                UICanvasCircle(
                    x = w * 0.67f,
                    y = h * 0.2f,
                    radius = w * 0.4f
                )
            )

            applyTheme(
                EZApp.theme
            )

            root.addView(
                this
            )
        }

        VEViewAVS(
            context
        ).apply {
            val canvasSize = (
                EZApp.width * 0.75f
                ).run {
                    Size(
                        this,
                        this
                    )
                }

            model = VEModelImport.createFromResource(
                resources,
                R.raw.avs_mouse,
                canvasSize
            ).apply {
                shapes.forEach {
                    it.color = EZApp.theme.colorIcon
                }
            }

            val currentX = EZApp.width * -0.5f

            shaper.addUpdateListener {
                val f = it.animatedValue as Float
                scaleX = 1.0f - 0.8f * f
                scaleY = scaleX
                translationX = currentX * f
            }

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = canvasSize.width,
                height = canvasSize.height,
                top = EZApp.height * 0.1f
            )

            root.addView(
                this
            )
        }

        ViewPager2(
            context
        ).apply {
            background = null
            id = ViewCompat.generateViewId()

            isUserInputEnabled = false

            val fragments = arrayOf(
                EZFragmentIntro().apply {
                    onClickLogin = View.OnClickListener {
                        onClickBtnLogin(it)
                    }
                    onClickSignIn = View.OnClickListener {
                        onClickBtnSignIn(it)
                    }
                },
                EZFragmentSignIn().apply {
                    onSignInSuccess = this@EZFragmentAuth
                },
                EZFragmentLogin().apply {
                    onLoginSuccess = this@EZFragmentAuth
                }
            )

            mViewPagerShaper = EZViewPagerShaper(
                this,
                shaper
            )

            adapter = EZAdapterPager(
                fragments,
                childFragmentManager,
                lifecycle
            )

            root.addView(
                this
            )
        }

        return@let root
    }

    override fun onLoginSuccess(
        user: EZModelUser
    ) {
        navigation?.push(
            EZFragmentCall().apply {
                fromUser = user
            }
        )
    }

    override fun onSignInSuccess() {

    }

    override fun backPressed() {
        mViewPagerShaper?.apply {
            if (currentItem == 0) {
                super.backPressed()
                return
            }
            pathAnimationReverse()
            currentItem = 0
        }

    }

    private inline fun onClickBtnSignIn(
        btn: View
    ) {
        mViewPagerShaper?.apply {
            pathAnimationDefault()
            prepareAnimation(
                mShapeAnimationLogin
            )
            currentItem = 1
        }
    }

    private inline fun onClickBtnLogin(
        btn: View
    ) {
        mViewPagerShaper?.apply {
            pathAnimationDefault()
            prepareAnimation(
                mShapeAnimationLogin
            )
            currentItem = 2
        }
    }

}