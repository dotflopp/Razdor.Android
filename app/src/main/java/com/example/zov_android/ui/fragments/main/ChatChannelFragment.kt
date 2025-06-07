package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import asFlow
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.data.signalr.SignalR
import com.example.zov_android.databinding.FragmentChatChannelBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.di.qualifiers.User
import com.example.zov_android.ui.adapters.ChatRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ChatChannelFragment(
    private val channelId: Long
) : NavigableFragment(), ChatRecyclerViewAdapter.Listener {
    private var _binding: FragmentChatChannelBinding? = null
    private val binding get() = _binding!!

    private var usersAdapter: ChatRecyclerViewAdapter? = null

    private val messagesViewModel: MessagesViewModel by viewModels()

    @Inject
    @Token
    lateinit var token: String

    @Inject
    @User
    lateinit var user: UserResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(context: Context): View {
        _binding = FragmentChatChannelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupRecyclerView()
        setupSendMessages()

        lifecycleScope.launch {
            messagesViewModel.signalR.newMessageEvent.asFlow().collect { message ->
                messagesViewModel.addNewMessage(message)
            }
        }

    }

    private fun setupSendMessages(){
        val file = File(context?.cacheDir, "test.jpg")

        binding.sendButton.setOnClickListener {
            val text = binding.editText.text.toString()
            messagesViewModel.loadMessages(token, requireContext(), channelId, text, null)
            binding.editText.text.clear()
        }

    }

    private fun setupRecyclerView() = with(binding){
        chatList.layoutManager = LinearLayoutManager(requireContext())
        usersAdapter = ChatRecyclerViewAdapter(this@ChatChannelFragment, user)
        chatList.adapter = usersAdapter
    }

    private fun init(){
        messagesViewModel.claimMessages(token,channelId)


        lifecycleScope.launch(Dispatchers.IO) {
            messagesViewModel.messagesListState.collect { state ->
                when (state) {
                    is BaseViewModel.ViewState.Success -> {
                        lifecycleScope.launch(Dispatchers.Main) {
                            val sortedList = state.data.reversed()
                            val adapter = binding.chatList.adapter as? ChatRecyclerViewAdapter
                            adapter?.updateList(sortedList)

                            // Прокрутка к последнему элементу
                            if (state.data.isNotEmpty()) {
                                binding.chatList.scrollToPosition(state.data.size - 1)
                            }
                        }
                    }
                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Loading ->{}
                    else -> {}
                }
            }
        }
    }

    override fun onChannelClick(idChannel: Long) {

    }

    override fun onDestroyView() {
        _binding = null
        usersAdapter = null
        super.onDestroyView()
    }

}