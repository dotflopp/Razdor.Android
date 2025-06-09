package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.zov_android.R
import com.example.zov_android.data.models.response.Attachment
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.databinding.ItemChatRecyclerViewBinding
import com.example.zov_android.domain.utils.UserCommunicationDisplayedStatus
import com.example.zov_android.ui.viewmodels.AttachmentViewModel
import com.example.zov_android.ui.viewmodels.MessagesViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ChatRecyclerViewAdapter(
    private val listener: Listener,
    private val user: UserResponse,
    private val token: String,
    private val channelId: Long,
    private val attachmentViewModel: AttachmentViewModel
)
    : RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRecyclerViewHolder>(){

    interface Listener{
        fun onProfileClick(idChannel:Long)
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

        memberMap = listMember.associateBy { it.userId }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerViewHolder {
        val binding = ItemChatRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatRecyclerViewHolder(binding, token, channelId, attachmentViewModel)
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

                /// --- Дата ---
                val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                utcFormatter.timeZone = TimeZone.getTimeZone("UTC") // Явно указываем, что входная дата в UTC

                val utcDate = utcFormatter.parse(message.createdAt)
                val deviceTimeZone = TimeZone.getDefault()

                val outputFormatter = SimpleDateFormat("dd MMMM, HH:mm", Locale("ru"))
                outputFormatter.timeZone = deviceTimeZone // Применяем часовой пояс устройства

                val formattedDate = outputFormatter.format(utcDate!!)
                senderDateTextView.text = formattedDate

                // --- Аватарки ---
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

                // --- Вложения ---
                if (message.attachments != null && message.attachments.isNotEmpty()) {
                    holder.setupRecyclerView()
                    holder.setupFileList(message.id, message.attachments)
                } else {
                    holder.binding.fileList.visibility = View.GONE
                }

            }

            holder.bind { listener.onProfileClick(idChannel = channelId)}

        }
    }

    class ChatRecyclerViewHolder(
        val binding: ItemChatRecyclerViewBinding,
        private val token: String,
        private val channelId: Long,
        private val attachmentViewModel: AttachmentViewModel
    )
        :RecyclerView.ViewHolder(binding.root), FileRecyclerViewAdapter.Listener{


            private val filesAdapter by lazy {
                FileRecyclerViewAdapter(this@ChatRecyclerViewHolder, token)
            }

            fun setupRecyclerView() {
                Log.d("FileAdapter", "Установка RecyclerView")
                with(binding) {
                    fileList.layoutManager = LinearLayoutManager(binding.root.context)
                    fileList.adapter = filesAdapter
                }
            }

            fun setupFileList(messageId:String, attachments: List<Attachment>) {
                Log.d("FileAdapter", "$attachments")
                binding.fileList.visibility = View.VISIBLE
                (binding.fileList.adapter as? FileRecyclerViewAdapter)?.updateList(messageId, attachments)
            }

            fun bind(onProfileClick: () -> Unit) {
                binding.apply {
                    anotherProfileImageView.setOnClickListener {
                        onProfileClick.invoke()
                    }
                }
            }

        override fun onFileClick(messageId: String, idFile: String, mediaType: String) {
            attachmentViewModel.setLastUsedMimeType(mediaType)
            attachmentViewModel.downloadAttachment(
                channelId,
                messageId.toLong(),
                idFile.toLong(),
                token
            )
        }
    }

}