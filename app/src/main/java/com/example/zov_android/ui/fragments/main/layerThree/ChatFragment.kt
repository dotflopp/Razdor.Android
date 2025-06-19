package com.example.zov_android.ui.fragments.main.layerThree

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.databinding.FragmentChatBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.ui.adapters.UsersRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : NavigableFragment(), UsersRecyclerViewAdapter.Listener {

    private var usersAdapter: UsersRecyclerViewAdapter? = null


    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var job: Job? = null // Добавляем контроль над корутиной


    override fun onCreateView(context: Context): View {
        _binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        init()
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


    private fun init() {
        // Отменяем предыдущую подписку перед созданием новой
        job?.cancel()

        job = viewLifecycleOwner.lifecycleScope.launch {
            // Используем repeatOnLifecycle для безопасной подписки
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.userState.collectLatest { state ->
                    when (state) {
                        is BaseViewModel.ViewState.Success -> {
                            updateUserList(state.data)
                        }
                        is BaseViewModel.ViewState.Error -> showError(state.message)
                        BaseViewModel.ViewState.Loading -> showLoading()
                        else -> {}
                    }
                }
            }
        }
    }
    private fun updateUserList(userData: UserResponse) {
        // Проверяем, что фрагмент все еще прикреплен
        if (!isAdded || _binding == null) return

        val users = listOf(
            Pair(userData.nickname, userData.selectedStatus.toString())
        )
        usersAdapter?.updateList(users)
    }

    private fun showLoading() {
        // Показать индикатор загрузки
    }

    private fun showError(message: String) {
        // Показать ошибку
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Отменяем корутину при уничтожении вью
        job?.cancel()
        job = null
        _binding = null
    }

}