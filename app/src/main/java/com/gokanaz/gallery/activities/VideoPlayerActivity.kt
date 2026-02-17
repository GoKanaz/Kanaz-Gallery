package com.gokanaz.gallery.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.gokanaz.gallery.R
import com.gokanaz.gallery.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var videoUri: Uri? = null
    private var isPrepared = false
    private var videoWidth = 0
    private var videoHeight = 0

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    binding.seekBar.progress = it.currentPosition
                    binding.currentTime.text = formatTime(it.currentPosition)
                }
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("video_uri", Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("video_uri")
        }

        if (videoUri == null) {
            val path = intent.getStringExtra("video_path")
            if (path != null) videoUri = Uri.parse(path)
        }

        binding.videoView.holder.addCallback(this)
        binding.videoControls.visibility = View.VISIBLE

        setupToolbar()
        setupListeners()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        initMediaPlayer(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        adjustVideoSize()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer?.setDisplay(null)
    }

    private fun initMediaPlayer(holder: SurfaceHolder) {
        val uri = videoUri ?: return
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDisplay(holder)
            setDataSource(this@VideoPlayerActivity, uri)
            setOnVideoSizeChangedListener { _, w, h ->
                videoWidth = w
                videoHeight = h
                adjustVideoSize()
            }
            setOnPreparedListener { mp ->
                isPrepared = true
                mp.start()
                binding.seekBar.max = mp.duration
                binding.totalTime.text = formatTime(mp.duration)
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                handler.postDelayed(runnable, 0)
            }
            setOnCompletionListener {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(runnable)
            }
            setOnErrorListener { _, _, _ -> false }
            prepareAsync()
        }
    }

    private fun adjustVideoSize() {
        if (videoWidth == 0 || videoHeight == 0) return
        val surfaceView = binding.videoView
        val viewWidth = surfaceView.width
        val viewHeight = surfaceView.height
        if (viewWidth == 0 || viewHeight == 0) return

        val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val screenRatio = viewWidth.toFloat() / viewHeight.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (videoRatio > screenRatio) {
            newWidth = viewWidth
            newHeight = (viewWidth / videoRatio).toInt()
        } else {
            newHeight = viewHeight
            newWidth = (viewHeight * videoRatio).toInt()
        }

        val params = surfaceView.layoutParams as ViewGroup.LayoutParams
        params.width = newWidth
        params.height = newHeight
        surfaceView.layoutParams = params
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
                if (fromUser && isPrepared) mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun toggleControls() {
        binding.videoControls.visibility =
            if (binding.videoControls.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun togglePlayPause() {
        if (!isPrepared) return
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
        return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
