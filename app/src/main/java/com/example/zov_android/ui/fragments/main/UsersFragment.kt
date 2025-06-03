package com.example.zov_android.ui.fragments.main


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.databinding.FragmentUsersBinding
import com.example.zov_android.ui.adapters.UsersRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : NavigableFragment(), UsersRecyclerViewAdapter.Listener{

    @Inject
    lateinit var mainRepository: MainRepository

    private var usersAdapter: UsersRecyclerViewAdapter? = null

    private lateinit var _binding: FragmentUsersBinding

    override fun onCreateView(context: Context): View {
        _binding = FragmentUsersBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() = with(_binding){
        usersList.layoutManager = LinearLayoutManager(requireContext()) // тут можно изменить то, как отображается наш список верт/гор
        usersAdapter = UsersRecyclerViewAdapter(this@UsersFragment)
        usersList.adapter = usersAdapter
    }



    //обработка вызова на стороне отправителя
    override fun onVideoCallClicked(username: String) {
        navigation.push(CallFragment().apply {
            arguments = Bundle().apply {
                putString("target", username)
                putBoolean("isVideoCall", true)
                putBoolean("isCaller", true)
            }
        })
    }

    override fun onAudioCallClicked(username: String) {
        navigation.push(CallFragment().apply {
            arguments = Bundle().apply {
                putString("target", username)
                putBoolean("isVideoCall", false)
                putBoolean("isCaller", true)
            }
        })
    }

    fun updateUsersList(users: List<Pair<String, String>>) {
        (_binding.usersList.adapter as? UsersRecyclerViewAdapter)?.updateList(users)
    }

    companion object {
        @JvmStatic
        fun newInstance() = UsersFragment()
    }
}