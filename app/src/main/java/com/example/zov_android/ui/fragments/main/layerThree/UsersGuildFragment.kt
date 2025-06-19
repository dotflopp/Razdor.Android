package com.example.zov_android.ui.fragments.main.layerThree

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.data.models.request.InvitesRequest
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.databinding.FragmentUsersGuildBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.ui.adapters.MembersGuildRecyclerViewAdapter
import com.example.zov_android.ui.adapters.UsersRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.GuildViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsersGuildFragment(
    private val idGuild: Long,
    private val membersGuild: List<MembersGuildResponse>
) : NavigableFragment(), MembersGuildRecyclerViewAdapter.Listener {
    private var _binding: FragmentUsersGuildBinding? = null
    private val binding get() = _binding!!

    private var usersAdapter: MembersGuildRecyclerViewAdapter? = null

    private val guildViewModel: GuildViewModel by viewModels()

    @Inject
    @Token
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guildViewModel.getMembersGuild(token, idGuild)
    }


    override fun onCreateView(context: Context): View {
        _binding = FragmentUsersGuildBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickNewInvitation()

        init()
    }


    private fun setupRecyclerView() = with(binding){
        usersList.layoutManager = LinearLayoutManager(requireContext())
        usersAdapter = MembersGuildRecyclerViewAdapter(this@UsersGuildFragment)
        usersList.adapter = usersAdapter
    }

    private fun init(){
        (_binding!!.usersList.adapter as? MembersGuildRecyclerViewAdapter)?.updateList(membersGuild)
        /*lifecycleScope.launch(Dispatchers.Main) {
            guildViewModel.guildMembersState.collect { state ->
                when (state) {
                    is BaseViewModel.ViewState.Success -> {
                        (_binding!!.usersList.adapter as? MembersGuildRecyclerViewAdapter)?.updateList(state.data)
                    }
                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Loading ->{}
                    else -> {}
                }
            }
        }*/

        binding.icBack.setOnClickListener {
            navigationInside.pop()
        }
    }

    private fun setupClickNewInvitation(){
        binding.createInvitation.setOnClickListener {
            guildViewModel.createInvitation(token,idGuild, InvitesRequest(lifeTime = null))
        }
    }

    override fun onVideoCallClicked(username: String) {
        TODO("Not yet implemented")
    }

    override fun onAudioCallClicked(username: String) {
        TODO("Not yet implemented")
    }


}