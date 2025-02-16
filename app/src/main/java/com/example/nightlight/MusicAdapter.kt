package com.example.nightlight

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MusicAdapter(
    private val musicList: List<Music>,
    private val onMusicSelected: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var selectedPosition = -1



    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val musicNameText: TextView = itemView.findViewById(R.id.musicNameText)
        private val cardView: CardView = itemView as CardView

        fun bind(music: Music, position: Int) {
            musicNameText.text = music.name

            // 设置选中状态
            cardView.setCardBackgroundColor(
                if (position == selectedPosition) {
                    Color.parseColor("#4DFFFFFF")
                } else {
                    Color.parseColor("#33FFFFFF")
                }
            )

            itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                onMusicSelected(music)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(musicList[position], position)
    }

    override fun getItemCount(): Int = musicList.size
}

data class Music(
    val name: String,
    val resourceId: Int
)
