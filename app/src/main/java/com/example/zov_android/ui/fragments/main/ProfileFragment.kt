package com.example.zov_android.ui.fragments.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.zov_android.R
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.databinding.FragmentProfileBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.di.qualifiers.User
import com.example.zov_android.domain.utils.UserCommunicationSelectedStatus
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : NavigableFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    @Inject
    @User
    lateinit var user: UserResponse

    @Inject
    @Token
    lateinit var token: String

    override fun onCreateView(context: Context): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            userViewModel.userState.collect { state ->
                when (state) {
                    is BaseViewModel.ViewState.Success -> {
                        updateUI(state.data)
                    }

                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            userViewModel.statusState.collect { state ->
                when (state) {
                    is BaseViewModel.ViewState.Success -> {
                        Toast.makeText(context, "Статус обновлен", Toast.LENGTH_SHORT).show()
                    }

                    is BaseViewModel.ViewState.Error -> {
                        Toast.makeText(context, "Ошибка: ${state.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }
        setupStatusButton()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: UserResponse) {
        when(user.selectedStatus.toString()) {
            "Online" -> binding.statusIndicator.setBackgroundResource(R.drawable.circle_green)
            "Invisible" -> binding.statusIndicator.setBackgroundResource(R.drawable.circle_yellow)
        }

        binding.username.text = user.nickname
        binding.identity.text = "#${user.identityName}"
        user.description?.let { binding.description.text = it }
    }

    private fun setupStatusButton() {
        binding.statusButton.setOnClickListener {
            showStatusSelectionDialog()
        }
    }

    private fun showStatusSelectionDialog() {
        val statuses = arrayOf("Онлайн", "Невидимка")

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Выберите статус")
            setItems(statuses) { _, which ->
                val status = when (which) {
                    0 -> StatusRequest(status = UserCommunicationSelectedStatus.Online.toString())

                    1 -> StatusRequest(status = UserCommunicationSelectedStatus.Invisible.toString())

                    else -> null
                }
                status?.let {
                    userViewModel.loadUserSelectedStatus(token, status)
                }
            }

        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}