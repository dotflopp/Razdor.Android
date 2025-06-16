package com.example.zov_android.ui.fragments.main.layerTwo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.databinding.FragmentMainBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.ui.adapters.GuildsRecyclerViewAdapter
import com.example.zov_android.ui.fragments.main.layerThree.GuildFragment
import com.example.zov_android.ui.fragments.main.layerThree.ChatFragment
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.GuildViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
                if(binding.nameGroup.text.toString().trim().isNotEmpty()){
                    guildViewModel.loadGuildData(token, GuildRequest(name = binding.nameGroup.text.toString()))

                    lifecycleScope.launch(Dispatchers.IO) {
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
                binding.nameGroup.text.clear()
            }

        }
    }

    fun handleBackPress(): Boolean {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            return true
        }
        return false
    }

    override fun onGuildClick(id:String, nameGuild:String) {
        navigationInsideFragment.push(GuildFragment(id, nameGuild))
    }

}