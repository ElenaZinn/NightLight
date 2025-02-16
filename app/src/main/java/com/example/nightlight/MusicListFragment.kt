package com.example.nightlight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MusicListFragment : Fragment() {
    interface OnMusicSelectedListener {
        fun onMusicSelected(musicRes: Int)
    }

    private var listener: OnMusicSelectedListener? = null
    private val musicList = listOf(
        Music("壁炉声", R.raw.rain_sound),
        Music("雨声", R.raw.fireplace_sound)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MusicAdapter(musicList) { music ->
            listener?.onMusicSelected(music.resourceId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMusicSelectedListener) {
            listener = context
        }
    }
}


