package com.gokanaz.gallery.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.gokanaz.gallery.R
import com.gokanaz.gallery.databinding.ActivityVideoPlayerBinding
import com.gokanaz.gallery.utils.Constants

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                binding.seekBar.progress = it.currentPosition
                binding.currentTime.text = formatTime(it.currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        playVideo()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        binding.videoView.setOnClickListener { toggleControls() }
        binding.btnPlayPause.setOnClickListener { togglePlayPause() }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playVideo() {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Constants.EXTRA_MEDIA_ID, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Uri>(Constants.EXTRA_MEDIA_ID)
        } ?: return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@VideoPlayerActivity, uri)
            setDisplay(binding.videoView.holder)
            setOnPreparedListener { mp ->
                mp.start()
                binding.seekBar.max = mp.duration
                binding.totalTime.text = formatTime(mp.duration)
                handler.postDelayed(runnable, 0)
            }
            setOnCompletionListener {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            prepareAsync()
        }
    }

    private fun toggleControls() {
        binding.videoControls.visibility =
            if (binding.videoControls.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                it.start()
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                handler.postDelayed(runnable, 0)
            }
        }
    }

    private fun formatTime(millis: Int): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
