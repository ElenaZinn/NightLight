package com.example.nightlight

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import com.google.android.material.button.MaterialButton

class TimerFragment : Fragment() {
    interface OnTimerSetListener {
        fun onTimerSet(minutes: Int)
    }

    private var listener: OnTimerSetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 120

        view.findViewById<MaterialButton>(R.id.confirmButton).setOnClickListener {
            listener?.onTimerSet(numberPicker.value)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTimerSetListener) {
            listener = context
        }
    }
}
