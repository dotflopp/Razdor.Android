package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.zov_android.R
import com.example.zov_android.data.models.response.Attachment
import com.example.zov_android.databinding.ItemFileRecyclerViewBinding

class FileRecyclerViewAdapter(private val listener: Listener, private val token:String)
    : RecyclerView.Adapter<FileRecyclerViewAdapter.FileRecyclerViewHolder>(){

    private var attachmentsList = mutableListOf<Attachment>()
    private var currentMessageId: String = ""
    interface Listener{
        fun onFileClick(messageId: String, idFile:String, mediaType: String)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(messageId:String, list: List<Attachment>){
        Log.d("FileAdapter", "New list size: ${list.size}")
        this.currentMessageId = messageId
        attachmentsList.clear()
        attachmentsList.addAll(list)

        Log.d("FileInsideAttachment", "$attachmentsList")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileRecyclerViewHolder {
        val binding = ItemFileRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FileRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return attachmentsList.size ?:0
    }

    override fun onBindViewHolder(holder: FileRecyclerViewHolder, position: Int) {
        attachmentsList.let {list->
            val file = list[position]

            val url = "https://dotflopp.ru" + file.sourceUrl + "?access-token=$token"
            Log.d("UrlImageInsideAttachment", file.fileName)

            with(holder.binding) {
                fileNameTextView.text = file.fileName ?: "Файл"

                if (file.mediaType.startsWith("image/")) {
                    fileImageView.load(url) {
                        placeholder(R.mipmap.ic_launcher)
                        error(R.mipmap.ic_launcher)
                    }
                } else {
                    fileImageView.load(R.drawable.ic_text)
                }
            }

            holder.bind {
                listener.onFileClick(currentMessageId, file.id, file.mediaType)
            }
        }
    }

    class FileRecyclerViewHolder(val binding: ItemFileRecyclerViewBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(onFileClick: () -> Unit) {
            binding.apply {
                fileImageView.setOnClickListener {
                    onFileClick.invoke()
                }
            }
        }

    }
}