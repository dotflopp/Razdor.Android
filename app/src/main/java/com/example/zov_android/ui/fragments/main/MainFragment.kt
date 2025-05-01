package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.R
import com.example.zov_android.ui.adapters.MainRecyclerViewAdapter
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.databinding.ActivityMainBinding
import com.example.zov_android.databinding.FragmentMainBinding
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.domain.service.MainServiceRepository
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.DataModelType
import com.example.zov_android.ui.activities.CallActivity
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : NavigableFragment(), MainRecyclerViewAdapter.Listener, MainService.Listener {

    @Inject
    lateinit var mainRepository: MainRepository
    @Inject
    lateinit var mainServiceRepository: MainServiceRepository
    private var mainAdapter: MainRecyclerViewAdapter? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(context: Context): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupRecyclerView()
        subscribeObservers()
    }

    private fun setupRecyclerView() {
        mainAdapter = MainRecyclerViewAdapter(this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = mainAdapter
        }
    }

    private fun subscribeObservers() {
        MainService.listener = this // прослушка входящих событий
        mainRepository.observeUsersStatus {
            mainAdapter?.updateList(it)
        }
    }

    //обработка вызова на стороне отправителя
    override fun onVideoCallClicked(username: String) {
        mainRepository.sendConnectionsRequest(username, true) {
            if (it) {
                startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                    putExtra("target", username)
                    putExtra("isVideoCall", true)
                    putExtra("isCaller", true)
                })
            }
        }
    }

    override fun onAudioCallClicked(username: String) {
        mainRepository.sendConnectionsRequest(username, false) {
            if (it) {
                startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                    putExtra("target", username)
                    putExtra("isVideoCall", false)
                    putExtra("isCaller", true)
                })
            }
        }
    }

    //обработка входящего вызова на стороне получателя
    override fun onCallReceived(model: DataModel) {
        // тк это событие приходит из другого потока, то делаем следующее
        requireActivity().runOnUiThread {
            //используем его в потокое интерфейса
            val isVideoCall = model.type == DataModelType.StartVideoCall
            val isVideoCallText = if (isVideoCall) "видео-" else "аудио-"

            binding.incomingCallTitleTv.text = "Входящий ${isVideoCallText}вызов от ${model.sender}"
            binding.incomingCallLayout.isVisible = true

            binding.acceptButton.setOnClickListener {  //уведомляем отправителя о принятии запроса

                binding.incomingCallLayout.isVisible = false
                startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                    putExtra("target", model.sender)
                    putExtra("isVideoCall", isVideoCall)
                    putExtra("isCaller", false)
                })
            }

            binding.declineButton.setOnClickListener {
                binding.incomingCallLayout.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}