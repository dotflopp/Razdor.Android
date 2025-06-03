package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.zov_android.R
import com.example.zov_android.databinding.ItemUsersRecyclerViewBinding


class UsersRecyclerViewAdapter(private val listener: Listener): RecyclerView.Adapter<UsersRecyclerViewAdapter.UsersRecyclerViewHolder>() {

    interface Listener{
        fun onVideoCallClicked(username:String)
        fun onAudioCallClicked(username:String)
    }

    private var usersList:List<Pair<String,String>>? = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list:List<Pair<String,String>>){
        Log.d("MyLog", "Data received in UsersRecyclerViewAdapter: $list")
        this.usersList = list
        notifyDataSetChanged() // уведомляет об изменении данных
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRecyclerViewHolder {
        val binding = ItemUsersRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsersRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return usersList?.size?:0
    }

    override fun onBindViewHolder(holder: UsersRecyclerViewHolder, position: Int) {
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


    class UsersRecyclerViewHolder(private val binding: ItemUsersRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind( //принимаем имя пользователя и его статус, видео и аудио возвращают имя пользователя
            user: Pair<String, String>,
            videoCallClicked: (String) -> Unit,
            audioCallClicked: (String) -> Unit
        ) {
            binding.apply {// создание нового объекта
                when (user.second) {
                    "Online" -> {
                        videoCallBtn.isVisible = true //кнопки видимы
                        audioCallBtn.isVisible = true
                        videoCallBtn.setOnClickListener {
                            it.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
                                it.animate().scaleX(1f).scaleY(1f).duration = 100
                            }
                            videoCallClicked.invoke(user.first)
                        }
                        audioCallBtn.setOnClickListener {
                            audioCallClicked.invoke(user.first)
                        }
                        statusTv.setTextColor(context.resources.getColor(R.color.light_green, null))
                        statusTv.text = "В сети"
                    }

                    "Offline" -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.red, null))
                        statusTv.text = "Не в сети"
                    }

                    "Invisible" -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.yellow, null))
                        statusTv.text = "Не в сети"
                    }
                }
                usernameTv.text = user.first
            }
        }
    }
}