package com.example.zov_android.ui.activities


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.R
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.domain.utils.getCameraAndMicPermission
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.data.repository.MainServiceRepository
import com.example.zov_android.data.utils.decodeToken
import com.example.zov_android.data.utils.parseSnowflake
import com.example.zov_android.ui.fragments.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var token: String? = null
    private var authResponse: AuthResponse? = null

    @Inject lateinit var mainRepository: MainRepository
    @Inject lateinit var mainServiceRepository: MainServiceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authResponse = intent.getSerializableExtra("userparams", AuthResponse::class.java)

        if (authResponse == null) {
            Log.e("Token", "AuthResponse is null")
            finish()
            return
        }
        else{
            token = authResponse!!.token
            Log.d("Token", "Полученный token: $token")
        }

        val decodedToken = decodeToken(token!!)
        val (timestamp, workerId, sequence) = parseSnowflake(decodedToken.userId)

        Log.d("Token", "User ID (Snowflake) token: ${decodedToken.userId}")
        Log.d("Token", "Creation Time token: ${decodedToken.creationTime}")
        Log.d("Token", "Signature token: ${decodedToken.signature}")

        Log.d("Token","Timestamp: $timestamp ms (${Instant.ofEpochMilli(timestamp)})")
        Log.d("Token","Worker ID: $workerId")
        Log.d("Token","Sequence: $sequence")

        checkPermissionsAndStartService()
        loadMainFragment()

        mainRepository.getYourself(token!!){ isSuccessful, message, user ->
            if(isSuccessful){
                Log.d("UserData", "User: $user")
            }
            else{
                Log.d("UserData", "$message")
            }
        }

        // mainRepository.getSpecificUser()

    }

    private fun loadMainFragment() {
        val fragment = MainFragment().apply {
            arguments = Bundle().apply {
                putString("userparams", token)
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
        token?.let { mainServiceRepository.startService(it) }
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