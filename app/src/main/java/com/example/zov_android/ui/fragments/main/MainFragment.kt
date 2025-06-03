package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.zov_android.R
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.databinding.FragmentMainBinding
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.data.repository.MainServiceRepository
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.DataModelType
import com.example.zov_android.ui.adapters.VpAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : NavigableFragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(context: Context): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
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
            navigationInside.push(ChatFragment(), "ChatFragment")
        }
    }


}