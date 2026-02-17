package com.gokanaz.gallery.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gokanaz.gallery.R
import com.gokanaz.gallery.adapters.MediaPagerAdapter
import com.gokanaz.gallery.databinding.ActivityPhotoDetailBinding
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.utils.FileUtils
import com.gokanaz.gallery.viewmodels.GalleryViewModel

class PhotoDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPhotoDetailBinding
    private lateinit var viewModel: GalleryViewModel
    private lateinit var adapter: MediaPagerAdapter
    private var mediaList: List<MediaModel> = emptyList()
    private var currentPosition = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        
        setupToolbar()
        setupViewPager()
        setupListeners()
        loadData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupViewPager() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                updateCounter()
                updateFavoriteButton()
            }
        })
    }
    
    private fun setupListeners() {
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
        
        binding.btnShare.setOnClickListener {
            shareCurrentMedia()
        }
        
        binding.btnDelete.setOnClickListener {
            deleteCurrentMedia()
        }
        
        binding.btnInfo.setOnClickListener {
            showMediaInfo()
        }
    }
    
    private fun loadData() {
        mediaList = viewModel.allMedia.value ?: emptyList()
        currentPosition = intent.getIntExtra(Constants.EXTRA_POSITION, 0)
        
        adapter = MediaPagerAdapter(this, mediaList)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(currentPosition, false)
        
        updateCounter()
        updateFavoriteButton()
    }
    
    private fun updateCounter() {
        binding.counter.text = "${currentPosition + 1}/${mediaList.size}"
    }
    
    private fun updateFavoriteButton() {
        val currentMedia = mediaList.getOrNull(currentPosition)
        currentMedia?.let {
            val isFavorite = viewModel.isFavorite(it.id)
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled 
                else R.drawable.ic_favorite_border
            )
        }
    }
    
    private fun toggleFavorite() {
        val currentMedia = mediaList.getOrNull(currentPosition)
        currentMedia?.let {
            viewModel.toggleFavorite(it)
            updateFavoriteButton()
        }
    }
    
    private fun shareCurrentMedia() {
        val currentMedia = mediaList.getOrNull(currentPosition)
        currentMedia?.let {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, it.uri)
                type = if (it.type == MediaModel.MediaType.PHOTO) "image/*" else "video/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }
    
    private fun deleteCurrentMedia() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.ok) { _, _ ->
                val currentMedia = mediaList.getOrNull(currentPosition)
                currentMedia?.let {
                    if (FileUtils.deleteFile(this, it.uri)) {
                        viewModel.loadMedia()
                        finish()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun showMediaInfo() {
        val currentMedia = mediaList.getOrNull(currentPosition)
        currentMedia?.let {
            val info = """
                Name: ${it.name}
                Path: ${it.path}
                Size: ${it.getFormattedSize()}
                Date: ${android.text.format.DateFormat.format("dd MMM yyyy HH:mm", it.dateAdded * 1000)}
                Resolution: ${it.getResolution()}
                ${if (it.type == MediaModel.MediaType.VIDEO) "Duration: ${it.getFormattedDuration()}" else ""}
            """.trimIndent()
            
            MaterialAlertDialogBuilder(this)
                .setTitle("Media Info")
                .setMessage(info)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
