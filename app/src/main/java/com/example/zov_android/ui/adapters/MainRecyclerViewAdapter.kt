package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.zov_android.R
import com.example.zov_android.databinding.ItemMainRecyclerViewBinding

class MainRecyclerViewAdapter(private val listener: Listener): RecyclerView.Adapter<MainRecyclerViewAdapter.MainRecyclerViewHolder>() {

    interface Listener{
        fun onVideoCallClicked(username:String)
        fun onAudioCallClicked(username:String)
    }

    private var usersList:List<Pair<String,String>>? = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list:List<Pair<String,String>>){
        this.usersList = list
        notifyDataSetChanged() // уведомляет об изменении данных, в будущем использовать notifyItemRangeInserted
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewHolder {
        val binding = ItemMainRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return usersList?.size?:0
    }

    override fun onBindViewHolder(holder: MainRecyclerViewHolder, position: Int) {
        //Привязывает данные к каждому элементу списка
        usersList?.let { list->
            val user = list[position]
            holder.bind(user,{//слушатели нажатия
                listener.onVideoCallClicked(it)
            },{
                listener.onAudioCallClicked(it)
            })
        }
    }


    class MainRecyclerViewHolder(private val binding: ItemMainRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind( //принимаем имя пользователя и его статус, видео и аудио возащают имя пользователя
            user: Pair<String, String>,
            videoCallClicked: (String) -> Unit,
            audioCallClicked: (String) -> Unit
        ) {
            binding.apply {// создание новго объекта
                when (user.second) {
                    "ONLINE" -> {
                        videoCallBtn.isVisible = true //кнопки видимы
                        audioCallBtn.isVisible = true
                        videoCallBtn.setOnClickListener {
                            videoCallClicked.invoke(user.first)
                        }
                        audioCallBtn.setOnClickListener {
                            audioCallClicked.invoke(user.first)
                        }
                        statusTv.setTextColor(context.resources.getColor(R.color.light_green, null))
                        statusTv.text = "В сети"
                    }

                    "OFFLINE" -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.red, null))
                        statusTv.text = "Не в сети"
                    }

                    "IN_CALL" -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.yellow, null))
                        statusTv.text = "Идёт разговор"
                    }
                }
                usernameTv.text = user.first
            }
        }
    }
}