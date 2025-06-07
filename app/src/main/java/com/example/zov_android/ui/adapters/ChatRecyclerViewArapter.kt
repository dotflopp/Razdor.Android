package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.databinding.ItemChatRecyclerViewBinding
import com.example.zov_android.di.qualifiers.User
import javax.inject.Inject

class ChatRecyclerViewAdapter(private val listener: Listener, private val user: UserResponse)
    : RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRecyclerViewHolder>(){


    interface Listener{
        fun onChannelClick(idChannel:Long)
    }
    private var chatList = mutableListOf<MessagesResponse>()

    // Полностью заменяет список (при загрузке с сервера)
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<MessagesResponse>){
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerViewHolder {
        val binding = ItemChatRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return chatList.size ?:0
    }

    override fun onBindViewHolder(holder: ChatRecyclerViewHolder, position: Int) {
        chatList.let { list->
            val message = list[position]

            with(holder.binding){
                messageTextView.text = message.text

                if(message.userId == user.id){
                    anotherProfileImageView.isVisible = false
                    profileImageView.isVisible = true
                }

                else{
                    anotherProfileImageView.isVisible = true
                    profileImageView.isVisible = false
                }

            }

            holder.bind { listener.onChannelClick(message.id.toLong()) }

        }
    }

    class ChatRecyclerViewHolder(val binding: ItemChatRecyclerViewBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(onChannelClick: () -> Unit) {
            binding.apply {
                anotherProfileImageView.setOnClickListener {
                    onChannelClick.invoke()
                }
            }
        }

    }


}