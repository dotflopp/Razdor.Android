package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.zov_android.R
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.databinding.ItemChatRecyclerViewBinding
import com.example.zov_android.di.qualifiers.User
import com.example.zov_android.domain.utils.UserCommunicationDisplayedStatus
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ChatRecyclerViewAdapter(private val listener: Listener, private val user: UserResponse)
    : RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRecyclerViewHolder>(){


    interface Listener{
        fun onChannelClick(idChannel:Long)
    }
    private var chatList = mutableListOf<MessagesResponse>()
    private var memberList = mutableListOf<MembersGuildResponse>()

    private var memberMap = mapOf<String, MembersGuildResponse>()

    // Полностью заменяет список (при загрузке с сервера)
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<MessagesResponse>, listMember:List<MembersGuildResponse>){
        chatList.clear()
        chatList.addAll(list)

        memberList.addAll(listMember)

        Log.d("ListChatRecycler", "$chatList")
        Log.d("ListChatRecycler", "$memberList")

        memberMap = listMember.associateBy { it.userId }

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
            val member = memberMap[message.userId]

            with(holder.binding){
                messageTextView.text = message.text

                // Парсим строку в объект LocalDateTime (или Date + Calendar)
                val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                utcFormatter.timeZone = TimeZone.getTimeZone("UTC") // Явно указываем, что входная дата в UTC

                val utcDate = utcFormatter.parse(message.createdAt)


                val deviceTimeZone = TimeZone.getDefault()

                val outputFormatter = SimpleDateFormat("dd MMMM, HH:mm", Locale("ru"))
                outputFormatter.timeZone = deviceTimeZone // Применяем часовой пояс устройства

                val formattedDate = outputFormatter.format(utcDate!!)
                senderDateTextView.text = formattedDate

                if(message.userId == user.id){
                    val url = "https://dotflopp.ru" + user.avatar
                    profileImage.load(url) {
                        placeholder(R.mipmap.ic_launcher) // картинка при загрузке
                        error(R.mipmap.ic_launcher)
                    }

                    anotherProfileImageView.isVisible = false
                    profileImageView.isVisible = true
                }
                else{
                    val url = "https://dotflopp.ru" + member?.avatar
                    anotherProfileImage.load(url) {
                        placeholder(R.mipmap.ic_launcher) // картинка при загрузке
                        error(R.mipmap.ic_launcher)
                    }
                    when (member?.status) {
                        UserCommunicationDisplayedStatus.Online -> statusIndicator.setBackgroundResource(R.drawable.circle_green)
                        UserCommunicationDisplayedStatus.Offline -> statusIndicator.setBackgroundResource(R.drawable.circle_red)
                        UserCommunicationDisplayedStatus.DoNotDisturb -> statusIndicator.setBackgroundResource(R.drawable.circle_yellow)
                        null -> {}
                    }

                    otherUsername.text = member?.nickname
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