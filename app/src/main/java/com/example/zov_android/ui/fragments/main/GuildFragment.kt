package com.example.zov_android.ui.fragments.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.databinding.FragmentGuildBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.domain.utils.ChannelType
import com.example.zov_android.ui.adapters.ChannelRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.GuildViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@AndroidEntryPoint
class GuildFragment(
    private val idGuild: String,
    private val nameGroup: String,
) : NavigableFragment(), ChannelRecyclerViewAdapter.Listener {
    private var _binding: FragmentGuildBinding? = null
    private val binding get() = _binding!!

    private val guildViewModel: GuildViewModel by viewModels()

    private var channelAdapter: ChannelRecyclerViewAdapter? = null

    private var membersGuild: List<MembersGuildResponse>? = null

    @Inject
    @Token
    lateinit var token:String

    private var selectedType: ChannelType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guildViewModel.getChannelList(token, idGuild.toLong())
        guildViewModel.getMembersGuild(token, idGuild.toLong())
    }

    override fun onCreateView(context: Context): View {
        _binding = FragmentGuildBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickNewChannel()
        setupMembersGuild()
        setupRecyclerView()
        init()

    }


    private fun setupClickNewChannel() {
        binding.createNewChannel.setOnClickListener {
            val currentParent = parentFragment?.parentFragment
            var baseMainFragment: BaseMainFragment? = null

            if (currentParent is BaseMainFragment) {
                baseMainFragment = currentParent
            }

            baseMainFragment?.let { fragment ->
                fragment.binding.createNewGroup.isVisible = true

                fragment.binding.typeButton.setOnClickListener {
                    showTypeSelectionDialog { type ->
                        selectedType = type
                        Toast.makeText(requireContext(), "Выбран тип: $type", Toast.LENGTH_SHORT).show()
                    }
                }

                fragment.binding.createButton.setOnClickListener {
                    if(fragment.binding.nameChannel.text.trim().isNotEmpty() &&
                        selectedType != null &&
                        fragment.binding.idParentChannel.text.trim().isNotEmpty()
                        ){

                        guildViewModel.loadChannelData(token, idGuild.toLong(), ChannelRequest(
                            name = fragment.binding.nameChannel.text.toString(),
                            type = selectedType!!,
                            parentId = fragment.binding.idParentChannel.text.toString().toLong()
                        ))

                        lifecycleScope.launch(Dispatchers.IO) {
                            guildViewModel.channelState.collect{state->
                                when(state){
                                    is BaseViewModel.ViewState.Success -> {
                                        guildViewModel.getChannelList(token, idGuild.toLong())
                                    }

                                    is BaseViewModel.ViewState.Error,
                                    BaseViewModel.ViewState.Idle,
                                    BaseViewModel.ViewState.Loading -> {}
                                }

                            }
                        }

                        fragment.binding.createNewGroup.isVisible = false
                    }
                }

                fragment.binding.cancelButton.setOnClickListener {
                    fragment.binding.createNewGroup.isVisible = false

                    fragment.binding.nameChannel.text.clear()
                    fragment.binding.idParentChannel.text.clear()
                }

            } ?: run {
                Log.e("GuildFragment", "MainBaseFragment не найден")
            }
        }
    }

    private fun setupRecyclerView() = with(binding){
        channelList.layoutManager = LinearLayoutManager(requireContext()) // тут можно изменить то, как отображается наш список верт/гор
        channelAdapter = ChannelRecyclerViewAdapter(this@GuildFragment)
        channelList.adapter = channelAdapter
    }

    private fun init() {
        binding.textNameGroup.text = nameGroup

        lifecycleScope.launch(Dispatchers.Main) {
            guildViewModel.guildMembersState.collect{state->
                when(state){
                    is BaseViewModel.ViewState.Success -> {
                        membersGuild = state.data
                    }

                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Idle,
                    BaseViewModel.ViewState.Loading->{}
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            guildViewModel.listChannelState.collect{state->
                when(state){
                    is BaseViewModel.ViewState.Success -> {
                        (_binding!!.channelList.adapter as? ChannelRecyclerViewAdapter)?.updateList(state.data)
                    }

                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Idle,
                    BaseViewModel.ViewState.Loading->{}
                }
            }
        }
    }

    private fun showTypeSelectionDialog(onTypeSelected: (ChannelType) -> Unit) {
        val types = arrayOf("Категория", "Текстовый", "Голосовой", "Ветвь")

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Выберите тип канала")
            setItems(types) { _, which ->
                val type = when (which) {
                    0 -> ChannelType.CategoryChannel
                    1 -> ChannelType.TextChannel
                    2 -> ChannelType.VoiceChannel
                    3 -> ChannelType.ForkChannel
                    else -> null
                }

                if (type!=null) {
                    onTypeSelected(type)
                }
            }
        }.show()
    }

    private fun setupMembersGuild() = with(binding){
        icUsers.setOnClickListener {
            if(membersGuild!=null) {
                navigationInside.push(
                    UsersGuildFragment(idGuild.toLong(), membersGuild!!),
                    "UsersGuildFragment"
                )
            }
        }
    }

    override fun onChannelClick(channelId: Long, channelName:String, channelType: ChannelType) {
        when (channelType) {
            ChannelType.VoiceChannel -> {
                navigationInside.push(CallFragment(channelId), "CallFragment")
            }
            ChannelType.TextChannel -> {
                if(membersGuild!=null) {
                    navigationInside.push(
                        ChatChannelFragment(channelId, channelName, membersGuild!!),
                        "TextChatFragment"
                    )
                }
            }

            ChannelType.CategoryChannel -> {}

            ChannelType.ForkChannel -> {}
        }
    }

}