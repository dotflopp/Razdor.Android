package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.zov_android.GuildFragment
import com.example.zov_android.R
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.databinding.FragmentMainBinding
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.data.repository.MainServiceRepository
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.DataModelType
import com.example.zov_android.ui.adapters.GuildsRecyclerViewAdapter
import com.example.zov_android.ui.adapters.UsersRecyclerViewAdapter
import com.example.zov_android.ui.adapters.VpAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.GuildViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : NavigableFragment(), GuildsRecyclerViewAdapter.Listener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    @Token
    lateinit var token: String

    private val guildViewModel: GuildViewModel by viewModels()

    private var usersAdapter: GuildsRecyclerViewAdapter? = null

    private val navigationInsideFragment: NavigationInsideFragment by lazy {
        NavigationInsideFragment(childFragmentManager, binding.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guildViewModel.getGuildsUser(token)
    }

    override fun onCreateView(context: Context): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            navigationInsideFragment.push(ChatFragment(), "ChatFragment")
        }

        setupClickChat()
        setupRecyclerView()
        init()
        setupClickNewGroup()
    }

    private fun init(){
        lifecycleScope.launch {
            guildViewModel.listGuildState.collect{state->
                when(state){
                    is BaseViewModel.ViewState.Success -> {
                        (_binding!!.guildList.adapter as? GuildsRecyclerViewAdapter)?.updateList(state.data)
                    }

                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Idle,
                    BaseViewModel.ViewState.Loading -> {}
                }
            }
        }
    }


    private fun setupRecyclerView() = with(binding){
        guildList.layoutManager = LinearLayoutManager(requireContext()) // тут можно изменить то, как отображается наш список верт/гор
        usersAdapter = GuildsRecyclerViewAdapter(this@MainFragment)
        guildList.adapter = usersAdapter
    }

    private fun setupClickChat(){
        binding.iconChat.setOnClickListener{
            navigationInsideFragment.push(ChatFragment(),"ChatFragment")
        }
    }

    private fun setupClickNewGroup(){
        binding.iconPlus.setOnClickListener {
            binding.createNewGroup.isVisible = true

            binding.acceptButton.setOnClickListener {
                if(binding.nameGroup.text!=null){
                    guildViewModel.loadGuildData(token, GuildRequest(name = binding.nameGroup.text.toString()))

                    lifecycleScope.launch {
                        guildViewModel.guildState.collect{state->
                            when(state){
                                is BaseViewModel.ViewState.Success->{
                                    guildViewModel.getGuildsUser(token)
                                }

                                is BaseViewModel.ViewState.Error,
                                BaseViewModel.ViewState.Idle,
                                BaseViewModel.ViewState.Loading -> {}
                            }

                        }
                    }
                    binding.createNewGroup.isVisible = false
                }
            }

            binding.cancelButton.setOnClickListener {
                binding.createNewGroup.isVisible = false
            }

        }
    }

    override fun onGuildClick(id:String, nameGuild:String) {
        navigationInsideFragment.push(GuildFragment(id, nameGuild))
    }

}