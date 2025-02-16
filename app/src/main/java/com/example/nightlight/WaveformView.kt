package com.example.nightlight

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bars = FloatArray(25) // 20条竖线
    private val amplitudeRatio = 1f

    private val barPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun updateAmplitude(amplitude: Float) {
        // 将新的振幅数据移入数组
        System.arraycopy(bars, 1, bars, 0, bars.size - 1)
        bars[bars.size - 1] = amplitude
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val barWidth = width / 30 // 竖线宽度

        // 绘制背景
        canvas.drawRect(0f, 0f, width, height, backgroundPaint)

        // 绘制竖线
        for (i in bars.indices) {
            val startX = (i * (width / bars.size)) + (width / bars.size / 2)
            val amplitude = bars[i] * amplitudeRatio
            val startY = height / 2 + amplitude
            val endY = height / 2 - amplitude

            canvas.drawLine(startX, startY, startX, endY, barPaint)
        }
    }
}

