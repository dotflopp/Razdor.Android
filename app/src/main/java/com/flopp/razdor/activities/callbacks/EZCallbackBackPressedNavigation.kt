package com.flopp.razdor.activities.callbacks

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.navigation.EZNavigationFragment

class EZCallbackBackPressedNavigation<
    T: EZFragmentNavigation
>(
    private val fragmentNavigation: EZNavigationFragment<T>,
    private val mActivity: AppCompatActivity
): OnBackPressedCallback(
    enabled = true
) {

    override fun handleOnBackPressed() {
        fragmentNavigation.last()
            ?.backPressed()

        if (fragmentNavigation.size <= 0) {
            mActivity.finish()
        }
    }
}