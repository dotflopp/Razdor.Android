package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zov_android.R
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.databinding.ItemChannelRecyclerViewBinding
import com.example.zov_android.domain.utils.ChannelType

class ChannelRecyclerViewAdapter(private val listener: Listener)
    :RecyclerView.Adapter<ChannelRecyclerViewAdapter.ChannelRecyclerViewHolder>(){
    interface Listener{
        fun onChannelClick(channelId: Long, channelName:String,channelType: ChannelType)
    }

    private var channelList:List<ChannelResponse>? = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ChannelResponse>){
        this.channelList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelRecyclerViewHolder {
        val binding = ItemChannelRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChannelRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return channelList?.size?:0
    }

    override fun onBindViewHolder(holder: ChannelRecyclerViewHolder, position: Int) {
        channelList?.let {list->
            val channel = list[position]

            with(holder.binding){
                when (channel.type) {
                    ChannelType.VoiceChannel -> icImage.setImageResource(R.drawable.ic_sound)
                    ChannelType.TextChannel -> icImage.setImageResource(R.drawable.ic_text)
                    ChannelType.CategoryChannel -> {}
                    ChannelType.ForkChannel -> {}
                }
                nameChannel.text = channel.name
                typeChannel.text = channel.type.toString()

            }

            holder.bind {
                listener.onChannelClick(channel.id.toLong(),channel.name, channel.type)
            }

        }
    }

    class ChannelRecyclerViewHolder(val binding: ItemChannelRecyclerViewBinding)
        :RecyclerView.ViewHolder(binding.root){
            fun bind(onChannelClick: () -> Unit) {
                binding.apply {
                    itemChannel.setOnClickListener {
                        onChannelClick.invoke()
                    }
                }
            }

    }
}