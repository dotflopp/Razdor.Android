package com.example.zov_android.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.zov_android.R
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.databinding.ItemGroupRecyclerViewBinding

class GuildsRecyclerViewAdapter(private val listener:Listener)
    :RecyclerView.Adapter<GuildsRecyclerViewAdapter.GuildsRecyclerViewHolder>() {

    interface Listener{
        fun onGuildClick(id:String, nameGuild: String)
    }

    private var guildList: List<GuildResponse>? = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list:List<GuildResponse>){
        this.guildList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuildsRecyclerViewHolder {
        val binding = ItemGroupRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GuildsRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuildsRecyclerViewHolder, position: Int) {
        guildList?.let { list->
            val guild = list[position]

            with(holder.binding){
                if (guild.avatar!=null){
                    profileImageView.load(guild.avatar) {
                        placeholder(R.mipmap.ic_launcher) // картинка при загрузке
                        error(R.mipmap.ic_launcher)
                    }
                }
            }

            holder.bind { listener.onGuildClick(guild.id, guild.name) }

        }
    }

    override fun getItemCount(): Int {
        return guildList?.size?:0
    }


    class GuildsRecyclerViewHolder(val binding: ItemGroupRecyclerViewBinding)
        :RecyclerView.ViewHolder(binding.root) {
            fun bind(onGuildClick: () -> Unit) {
                binding.apply {
                    profileImageView.setOnClickListener {
                        onGuildClick.invoke()
                    }
                }
            }
    }


}