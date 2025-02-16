package com.example.nightlight

import android.animation.ArgbEvaluator
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MusicListFragment.OnMusicSelectedListener,
    TimerFragment.OnTimerSetListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var lightSeekBar: SeekBar
    private lateinit var diamondLight: View
    private lateinit var waveformView: WaveformView
    private lateinit var playPauseButton: ImageButton


    // 添加当前音效追踪变量
    private var currentSound = R.raw.rain_sound

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var isPlaying = true
    private var updateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lightSeekBar = findViewById(R.id.lightSeekBar)
        diamondLight = findViewById(R.id.diamondLight)
        waveformView = findViewById(R.id.waveformView)
        playPauseButton = findViewById(R.id.playPauseButton)

        playPauseButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                playPauseButton.setImageResource(R.drawable.ic_pause)
                mediaPlayer.start()  // 开始播放声音
                startWaveformUpdate()
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play)
                mediaPlayer.pause()  // 暂停声音
                updateJob?.cancel()
            }
        }

        setupLightControl()
        setupSoundControl()
        startWaveformUpdate()
    }

    private fun setupLightControl() {
        lightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 计算白光到黄光的渐变
                val color = calculateColor(progress)
                diamondLight.setBackgroundTintList(ColorStateList.valueOf(color))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun calculateColor(progress: Int): Int {
        val white = Color.parseColor("#FFFFFF")
        val yellow = Color.parseColor("#FFFF00")
        return ArgbEvaluator().evaluate(
            progress / 100f,
            white,
            yellow
        ) as Int
    }

    private fun setupSoundControl() {
        findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            // 切换音效
//            switchSound()
            showSettingsDialog()
        }

        // 初始化音频播放
        mediaPlayer = MediaPlayer.create(this, R.raw.rain_sound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun switchSound() {
        // 停止并释放旧的MediaPlayer
        mediaPlayer.stop()
        mediaPlayer.release()

        // 切换音效
        currentSound = if (currentSound == R.raw.rain_sound) {
            R.raw.fireplace_sound
        } else {
            R.raw.rain_sound
        }

        // 创建新的MediaPlayer并开始播放
        mediaPlayer = MediaPlayer.create(this, currentSound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        bottomSheetDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        updateJob?.cancel()
    }

    private fun showSettingsDialog() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)


        // 先加载布局
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)

        // 从加载的布局中查找视图
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // 设置适配器
        viewPager.adapter = SettingsPagerAdapter(this)

        // 关联TabLayout和ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "音乐列表"
                1 -> "定时设置"
                else -> ""
            }
        }.attach()

        // 设置内容视图
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


    override fun onMusicSelected(musicRes: Int) {
        // 处理音乐选择
        currentSound = musicRes
        switchSound()
    }

    override fun onTimerSet(minutes: Int) {
        // 处理定时设置
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.pause()  // 暂停声音
            stopWaveform()
            isPlaying = false
        }, minutes * 60 * 1000L)
        bottomSheetDialog.dismiss()
    }

    private fun startWaveformUpdate() {
        updateJob?.cancel()
        updateJob = lifecycleScope.launch {
            while (isActive && isPlaying) {
                val amplitude = (-0.5f + Math.random().toFloat()) * 100
                waveformView.updateAmplitude(amplitude)
                delay(50)
            }
        }
    }

    // 在倒计时结束的回调中调用这个方法
    private fun stopWaveform() {
        isPlaying = false
        updateJob?.cancel()
        playPauseButton.setImageResource(R.drawable.ic_play)
    }
}
