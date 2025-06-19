package com.example.zov_android.ui.fragments.main.layerOne

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.zov_android.R
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.databinding.FragmentBaseMainBinding
import com.example.zov_android.di.qualifiers.User
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.ui.fragments.main.layerTwo.MainFragment
import com.example.zov_android.ui.fragments.main.layerTwo.ProfileFragment
import com.example.zov_android.ui.fragments.main.layerThree.UsersGuildFragment
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BaseMainFragment : NavigableFragment(), MainService.Listener {
    private var _binding: FragmentBaseMainBinding? = null
    val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(context: Context): View {
        _binding = FragmentBaseMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerInside: FrameLayout = binding.container
        val navigationInside = NavigationInsideFragment(
            childFragmentManager = childFragmentManager,
            container = containerInside
        )

        if (savedInstanceState == null) {
            navigationInside.push(MainFragment(), "MainFragment")
        }

        //свой возврат "назад" для вложенных списков
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )


        binding.iconProfile.setOnClickListener {
            val tag = "ProfileFragment"
            if (!isFragmentAlreadyAdded(tag)) {
                navigationInside.pushUp(ProfileFragment(), tag)
            }
            else{
                childFragmentManager.popBackStack()
            }
        }

        binding.icHome.setOnClickListener {
            val tag = "MainFragment"
            if (!isFragmentAlreadyAdded(tag)) {
                navigationInside.push(MainFragment(), tag)
            }
            else{
                childFragmentManager.popBackStack()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){
            userViewModel.userState.collectLatest { state ->
                when(state) {
                    is BaseViewModel.ViewState.Success -> {
                        updateStatusIndicator(state.data)

                        if (state.data.avatar != null){
                            val url = "https://dotflopp.ru"+state.data.avatar
                            binding.iconProfile.load(url) {
                                placeholder(R.drawable.mouseicon) // картинка при загрузке
                                error(R.drawable.mouseicon)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateStatusIndicator(user: UserResponse) {
        when(user.selectedStatus.toString()) {
            "Online" -> binding.statusIndicator.setBackgroundResource(R.drawable.circle_green)
            "Invisible" -> binding.statusIndicator.setBackgroundResource(R.drawable.circle_yellow)
        }
    }

    fun handleBackPress(): Boolean {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            return true
        }

        val currentFragment = childFragmentManager.primaryNavigationFragment
        Log.d("BackPress", "Текущий фрагмент: $currentFragment")

        if (currentFragment is UsersGuildFragment) {

            return true
        }

        return false
    }


     private fun isFragmentAlreadyAdded(tag: String): Boolean {
        return childFragmentManager.findFragmentByTag(tag) != null
     }

    //обработка входящего вызова на стороне получателя
    override fun onCallReceived(model: DataModel) {
        // тк это событие приходит из другого потока, то делаем следующее
        /*requireActivity().runOnUiThread {
            //используем его в потокое интерфейса
            val isVideoCall = model.type == DataModelType.StartVideoCall
            val isVideoCallText = if (isVideoCall) "видео-" else "аудио-"

            binding.incomingCallTitleTv.text = "Входящий ${isVideoCallText}вызов от ${model.sender}"
            binding.incomingCallLayout.isVisible = true

            binding.acceptButton.setOnClickListener {  //уведомляем отправителя о принятии запроса

                binding.incomingCallLayout.isVisible = false
                /*navigation.push(CallFragment().apply {
                    arguments = Bundle().apply {
                        putString("target", "lukus")
                        putBoolean("isVideoCall", true)
                        putBoolean("isCaller", true)
                    }
                })*/
            }

            binding.declineButton.setOnClickListener {
                binding.incomingCallLayout.isVisible = false
            }
        }*/
    }
}