package com.example.musicplayer

import SoundItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SoundAdapter(
    private val soundList: List<SoundItem>,
    private val resourceId: Int
) : RecyclerView.Adapter<SoundAdapter.SoundViewHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val soundNameTextView: TextView = itemView.findViewById(R.id.soundNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(resourceId, parent, false)
        return SoundViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val currentSound = soundList[position]
        holder.soundNameTextView.text = currentSound.name

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(currentSound.name)
        }
    }

    override fun getItemCount(): Int {
        return soundList.size
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }
}
