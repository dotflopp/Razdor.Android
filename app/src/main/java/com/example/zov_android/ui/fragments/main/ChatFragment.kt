package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.databinding.FragmentChatBinding
import com.example.zov_android.ui.adapters.UsersRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : NavigableFragment(), UsersRecyclerViewAdapter.Listener {

    private var usersAdapter: UsersRecyclerViewAdapter? = null


    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(context: Context): View {
        _binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connection()
        init()
        setupRecyclerView()
    }

    private fun setupRecyclerView() = with(binding){
        usersList.layoutManager = LinearLayoutManager(requireContext()) // тут можно изменить то, как отображается наш список верт/гор
        usersAdapter = UsersRecyclerViewAdapter(this@ChatFragment)
        usersList.adapter = usersAdapter
    }



    //обработка вызова на стороне отправителя
    override fun onVideoCallClicked(username: String) {
        /*navigation.push(CallFragment().apply {
            arguments = Bundle().apply {
                putString("target", username)
                putBoolean("isVideoCall", true)
                putBoolean("isCaller", true)
            }
        })*/
    }

    override fun onAudioCallClicked(username: String) {
        /*navigation.push(CallFragment().apply {
            arguments = Bundle().apply {
                putString("target", username)
                putBoolean("isVideoCall", false)
                putBoolean("isCaller", true)
            }
        })*/
    }

    private fun connection() = with(_binding){
        /*this?.btnConnection?.setOnClickListener {
            navigation.push(CallFragment().apply {
                arguments = Bundle().apply {
                    putString("target", "lukus")
                    putBoolean("isVideoCall", true)
                    putBoolean("isCaller", true)
                }
            })
        }*/
    }

    private fun init(){
        lifecycleScope.launch(Dispatchers.Main) {
            userViewModel.userState.collectLatest { state ->
                when (state) {
                    is BaseViewModel.ViewState.Success -> {
                        val users = listOf(
                            Pair(state.data.nickname, state.data.selectedStatus.toString())
                        )
                        (_binding!!.usersList.adapter as? UsersRecyclerViewAdapter)?.updateList(users)
                    }
                    is BaseViewModel.ViewState.Error -> showError(state.message)
                    BaseViewModel.ViewState.Loading -> showLoading()
                    else -> {}
                }
            }
        }
    }

    private fun showLoading() {
        // Показать индикатор загрузки
    }

    private fun showError(message: String) {
        // Показать ошибку
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}