package com.example.zov_android.ui.activities


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.R
import com.example.zov_android.domain.utils.getCameraAndMicPermission
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.domain.service.MainServiceRepository
import com.example.zov_android.ui.fragments.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var username: String? = null

    @Inject lateinit var mainRepository: MainRepository
    @Inject lateinit var mainServiceRepository: MainServiceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("username")
        if (username == null) finish()

        checkPermissionsAndStartService()
        loadMainFragment()
    }

    private fun loadMainFragment() {
        val fragment = MainFragment().apply {
            arguments = Bundle().apply {
                putString("username", username)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun checkPermissionsAndStartService() {
        getCameraAndMicPermission {
            startMyService()
        }
    }

    private fun startMyService() {
        username?.let { mainServiceRepository.startService(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startMyService()
        } else {
            Toast.makeText(this, "Отказано в разрешениях", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        mainServiceRepository.stopService()
    }
}