package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.databinding.FragmentFriendsBinding
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.ui.activities.CallActivity
import com.example.zov_android.ui.adapters.FriendsRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment : NavigableFragment(), FriendsRecyclerViewAdapter.Listener {

    @Inject
    lateinit var mainRepository: MainRepository

    private var friendsAdapter: FriendsRecyclerViewAdapter? = null

    private lateinit var _binding: FragmentFriendsBinding
    //private val model: MainViewModel by activityViewModels()

    override fun onCreateView(context: Context): View {
        _binding = FragmentFriendsBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initItemsListOne()

        //получаем отпаршеный список, когда прогружается вьюшка
        /*model.liveDataList.observe(viewLifecycleOwner){
            initItemsListTwo(it)
        }*/
        initItemsListTwo()
    }

    private fun initItemsListOne() = with(_binding){
        friendsList.layoutManager = LinearLayoutManager(requireContext()) // тут можно изменить то, как отображается наш список верт/гор
    }

    // items:List<Friends>
    private fun initItemsListTwo() = with(_binding){
        friendsAdapter = FriendsRecyclerViewAdapter(this@FriendsFragment)
        friendsList.adapter = friendsAdapter
    }

    //обработка вызова на стороне отправителя
    override fun onVideoCallClicked(username: String) {
        mainRepository.sendConnectionsRequest(username, true) {
            if (it) {
                startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                    putExtra("target", username)
                    putExtra("isVideoCall", true)
                    putExtra("isCaller", true)
                })
            }
        }
    }

    override fun onAudioCallClicked(username: String) {
        mainRepository.sendConnectionsRequest(username, false) {
            if (it) {
                startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                    putExtra("target", username)
                    putExtra("isVideoCall", false)
                    putExtra("isCaller", true)
                })
            }
        }
    }

    fun updateFriendsList(users: List<Pair<String, String>>) {
        (_binding.friendsList.adapter as? FriendsRecyclerViewAdapter)?.updateList(users)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FriendsFragment()
    }
}