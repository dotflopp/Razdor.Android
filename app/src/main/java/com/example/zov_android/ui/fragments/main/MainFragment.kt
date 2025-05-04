package com.example.zov_android.ui.fragments.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.zov_android.ui.adapters.FriendsRecyclerViewAdapter
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.databinding.FragmentMainBinding
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.domain.service.MainServiceRepository
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.DataModelType
import com.example.zov_android.ui.activities.CallActivity
import com.example.zov_android.ui.adapters.VpAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : NavigableFragment(), MainService.Listener {

    @Inject
    lateinit var mainRepository: MainRepository
    @Inject
    lateinit var mainServiceRepository: MainServiceRepository
    private var friendsAdapter: FriendsRecyclerViewAdapter? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val fList = listOf(
        FriendsFragment.newInstance(),
        //GroupsFragment.newInstance()
    )
    private val tList = listOf(
        "Личка",
        "Группы"
    )



    override fun onCreateView(context: Context): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        subscribeObservers()
        setupRecyclerView()
    }

    private fun setupRecyclerView() = with(_binding) {
        //search.setIconifiedByDefault(false)

        val adapter = VpAdapter(activity as FragmentActivity, fList)
        this?.vp!!.adapter = adapter

        TabLayoutMediator(tabLayout, vp){
                tab, pos -> tab.text = tList[pos]
        }.attach()

        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Здесь можно добавить код обновления фрагмента при его переключении
                val fragment = fList[position]
                if (fragment is FriendsFragment) {
                    Log.d("MyLog", "Friends")

                    mainRepository.observeUsersStatus { users ->
                        fragment.updateFriendsList(users)
                    }

                    //model.favData.value = ItemsAdapter.fafv.asd
                    //model.liveDataList.value = ll
                    /*if (arr.isEmpty() and arrf.isEmpty()) {
                        model.favData.value = ItemsAdapter.fafv.asd
                        model.liveDataList.value = ll
                    }
                    else {
                        model.favData.value = arrf
                        model.liveDataList.value = arr
                    }*/



                } /*else if (fragment is GroupeFragment) {
                    Log.d("MyLog", "fav")
                    if (arr.isEmpty() and arrf.isEmpty()) {
                        model.favData.value = ItemsAdapter.fafv.asd
                        model.liveDataList.value = ll
                    }
                    else {
                        model.favData.value = arrf
                        model.liveDataList.value = arr
                    }
                }*/

            }
        })
    }

    //получаем данные из firebase
    private fun subscribeObservers() {
        MainService.listener = this // прослушка входящих событий
        mainRepository.observeUsersStatus {
            friendsAdapter?.updateList(it)
            Log.d("MyLog", "Data received in MainFragment: $it")
        }
    }


    //обработка входящего вызова на стороне получателя
    override fun onCallReceived(model: DataModel) {
        // тк это событие приходит из другого потока, то делаем следующее
        requireActivity().runOnUiThread {
            //используем его в потокое интерфейса
            val isVideoCall = model.type == DataModelType.StartVideoCall
            val isVideoCallText = if (isVideoCall) "видео-" else "аудио-"

            binding.incomingCallTitleTv.text = "Входящий ${isVideoCallText}вызов от ${model.sender}"
            binding.incomingCallLayout.isVisible = true

            binding.acceptButton.setOnClickListener {  //уведомляем отправителя о принятии запроса

                binding.incomingCallLayout.isVisible = false
                startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                    putExtra("target", model.sender)
                    putExtra("isVideoCall", isVideoCall)
                    putExtra("isCaller", false)
                })
            }

            binding.declineButton.setOnClickListener {
                binding.incomingCallLayout.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}