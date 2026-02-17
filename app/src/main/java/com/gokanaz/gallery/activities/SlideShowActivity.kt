package com.gokanaz.gallery.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gokanaz.gallery.R
import com.gokanaz.gallery.adapters.MediaPagerAdapter
import com.gokanaz.gallery.databinding.ActivitySlideshowBinding
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.utils.Constants

class SlideShowActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySlideshowBinding
    private lateinit var adapter: MediaPagerAdapter
    private var mediaList: List<MediaModel> = emptyList()
    private var currentPosition = 0
    private var isPlaying = true
    private var delay = Constants.SLIDESHOW_DEFAULT_DELAY
    private val handler = Handler(Looper.getMainLooper())
    
    private val slideRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                val nextPosition = (currentPosition + 1) % mediaList.size
                binding.viewPager.setCurrentItem(nextPosition, true)
                handler.postDelayed(this, delay)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideshowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        mediaList = intent.getParcelableArrayListExtra(Constants.EXTRA_MEDIA_LIST) ?: emptyList()
        
        setupToolbar()
        setupViewPager()
        setupListeners()
        startSlideshow()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupViewPager() {
        adapter = MediaPagerAdapter(this, mediaList)
        binding.viewPager.adapter = adapter
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }
        })
    }
    
    private fun setupListeners() {
        binding.btnPlayPause.setOnClickListener {
            togglePlayPause()
        }
        
        binding.speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                delay = progress.toLong()
                binding.speedText.text = "${progress / 1000}s"
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                restartSlideshow()
            }
        })
    }
    
    private fun startSlideshow() {
        handler.postDelayed(slideRunnable, delay)
    }
    
    private fun togglePlayPause() {
        isPlaying = !isPlaying
        binding.btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        if (isPlaying) {
            handler.postDelayed(slideRunnable, delay)
        } else {
            handler.removeCallbacks(slideRunnable)
        }
    }
    
    private fun restartSlideshow() {
        handler.removeCallbacks(slideRunnable)
        if (isPlaying) {
            handler.postDelayed(slideRunnable, delay)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(slideRunnable)
    }
}
