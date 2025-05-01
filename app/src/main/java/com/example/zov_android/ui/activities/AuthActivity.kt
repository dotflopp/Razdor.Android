package com.example.zov_android.ui.activities

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.R
import com.example.zov_android.ui.fragments.auth.LoginFragment
import com.example.zov_android.ui.fragments.navigation.NavigationFragment
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var navigationFragment: NavigationFragment<NavigableFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val fragmentManager = supportFragmentManager
        val container = findViewById<FrameLayout>(R.id.fragment_container)

        navigationFragment = NavigationFragment(fragmentManager, container)

        if (savedInstanceState == null) {
            // Загрузка начального фрагмента
            navigateTo(LoginFragment())
        }
    }

    private fun navigateTo(fragment: NavigableFragment) {
        navigationFragment.push(fragment)
    }
}