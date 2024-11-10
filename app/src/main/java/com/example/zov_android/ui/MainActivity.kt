package com.example.zov_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.adapters.MainRecyclerViewAdapter
import com.example.zov_android.databinding.ActivityMainBinding
import com.example.zov_android.utils.getCameraAndMicPermission
import com.example.zov_android.repository.MainRepository
import com.example.zov_android.service.MainService
import com.example.zov_android.repository.MainServiceRepository
import com.example.zov_android.utils.DataModel
import com.example.zov_android.utils.DataModelType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity(), MainRecyclerViewAdapter.Listener, MainService.Listener {

    private lateinit var views: ActivityMainBinding
    private var username:String? = null
   // private var waitingResponse:Boolean = false

    @Inject lateinit var mainRepository: MainRepository
    @Inject lateinit var mainServiceRepository: MainServiceRepository
    private var mainAdapter: MainRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init(){
        username = intent.getStringExtra("username")
        if(username == null) finish() // завершаем действие и возвращаемся к предыдущему
        // отслеживаем других пользователей
        subscribeObservers()
        // проверяем разрешения перед запуском службы
        checkPermissionsAndStartService()
    }

    private fun subscribeObservers(){
        setupRecyclerView()
        MainService.listener = this // прослушка входящих событий
        mainRepository.observeUsersStatus {
            Log.d("MainActivity","observers:$it" )
            mainAdapter?.updateList(it)
        }
    }

    private fun setupRecyclerView(){
        mainAdapter = MainRecyclerViewAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        views.mainRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = mainAdapter
        }
    }

    //обработка вызова на стороне отправителя
    override fun onVideoCallClicked(username: String) {
        mainRepository.sendConnectionsRequest(username, true){
            if(it){ //если запрос успешен, начинаем видео звонок
                //создаём intent перехода к вызову
                 startActivity(Intent(this, CallActivity::class.java).apply {
                     putExtra("target", username)
                     putExtra("isVideoCall", true)
                     putExtra("isCaller", true)
                 })

            }
        }
    }

    override fun onAudioCallClicked(username: String) {
        mainRepository.sendConnectionsRequest(username, false){
            if(it){ //если запрос успешен, начинаем аудио звонок
                //функиця ожидания ответа от получателя waitingResponse()

                startActivity(Intent(this, CallActivity::class.java).apply {
                    putExtra("target", username)
                    putExtra("isVideoCall", false)
                    putExtra("isCaller", true)
                })

            }
        }
    }

    //обработка входящего вызова на стороне получателя
    @SuppressLint("SetTextI18n")
    override fun onCallReceived(model: DataModel) {
        // тк это событие приходит из другого потока, то делаем следующее
        runOnUiThread { //используем его в потокое интерфейса
            views.apply {

                val isVideoCall = model.type == DataModelType.StartVideoCall
                val isVideoCallText = if(isVideoCall) "видео-" else "аудио-"

                incomingCallTitleTv.text = "Входящий ${isVideoCallText}вызов от ${model.sender}"
                incomingCallLayout.isVisible = true

                acceptButton.setOnClickListener{

                    //уведомляем отправителя о принятии запроса
                   //

                    incomingCallLayout.isVisible = false

                    //переход в Intent звонка
                    startActivity(Intent(this@MainActivity,CallActivity::class.java).apply {
                        putExtra("target", model.sender)
                        putExtra("isVideoCall", isVideoCall)
                        putExtra("isCaller", false)
                    })
                }

                declineButton.setOnClickListener {
                    incomingCallLayout.isVisible = false
                }
            }
        }
    }

    private fun startMyService(){
        mainServiceRepository.startService(username!!)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        mainServiceRepository.stopService()
    }

    // проверка разрешений
    private fun checkPermissionsAndStartService() {
        getCameraAndMicPermission {
            // Запуск службы, если все необходимые разрешения предоставлены
            startMyService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Если все разрешения даны, запускам службы
                startMyService()
            } else {
                Toast.makeText(this@MainActivity, "Отказано в одном или нескольких разрешениях", Toast.LENGTH_SHORT).show()
            }
        }
    }


}