package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.zov_android.R
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.databinding.ItemUsersRecyclerViewBinding
import com.example.zov_android.domain.utils.UserCommunicationDisplayedStatus

class MembersGuildRecyclerViewAdapter(private val listener: Listener)
    : RecyclerView.Adapter<MembersGuildRecyclerViewAdapter.MembersGuildRecyclerViewHolder>(){
    interface Listener{
        fun onVideoCallClicked(username:String)
        fun onAudioCallClicked(username:String)
    }

    private var usersList:List<MembersGuildResponse>? = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list:List<MembersGuildResponse>){
        Log.d("MyLog", "Data received in UsersRecyclerViewAdapter: $list")
        this.usersList = list
        notifyDataSetChanged() // уведомляет об изменении данных
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersGuildRecyclerViewHolder {
        val binding = ItemUsersRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MembersGuildRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return usersList?.size?:0
    }

    override fun onBindViewHolder(holder: MembersGuildRecyclerViewHolder, position: Int) {
        //Привязывает данные к каждому элементу списка
        usersList?.let { list->
            val user = list[position]
            Log.d("MyLog", "Binding user at position $position: $user")
            holder.bind(user,{//слушатели нажатия
                listener.onVideoCallClicked(it)
            },{
                listener.onAudioCallClicked(it)
            })
        }
    }


    class MembersGuildRecyclerViewHolder(private val binding: ItemUsersRecyclerViewBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind( //принимаем имя пользователя и его статус, видео и аудио возвращают имя пользователя
            user: MembersGuildResponse,
            videoCallClicked: (String) -> Unit,
            audioCallClicked: (String) -> Unit
        ) {
            binding.apply {// создание нового объекта
                when (user.status) {
                    UserCommunicationDisplayedStatus.Online -> {
                        videoCallBtn.isVisible = true //кнопки видимы
                        audioCallBtn.isVisible = true
                        videoCallBtn.setOnClickListener {
                            it.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
                                it.animate().scaleX(1f).scaleY(1f).duration = 100
                            }
                            videoCallClicked.invoke(user.nickname)
                        }
                        audioCallBtn.setOnClickListener {
                            audioCallClicked.invoke(user.nickname)
                        }
                        statusTv.setTextColor(context.resources.getColor(R.color.light_green, null))
                        statusTv.text = "В сети"
                    }

                    UserCommunicationDisplayedStatus.Offline -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.red, null))
                        statusTv.text = "Не в сети"
                    }

                    UserCommunicationDisplayedStatus.DoNotDisturb -> {}
                }
                username.text = user.nickname
            }
        }
    }
}