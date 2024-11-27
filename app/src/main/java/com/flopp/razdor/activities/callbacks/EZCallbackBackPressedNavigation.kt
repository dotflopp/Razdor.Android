package com.flopp.razdor.activities.callbacks

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flopp.razdor.navigation.EZNavigationFragment

class EZCallbackBackPressedNavigation<
    T: Fragment
>(
    private val fragmentNavigation: EZNavigationFragment<T>,
    private val mActivity: AppCompatActivity
): OnBackPressedCallback(
    enabled = true
) {

    override fun handleOnBackPressed() {
        if (fragmentNavigation.size <= 1) {
            mActivity.finish()
            return
        }

        fragmentNavigation.pop()
    }
}