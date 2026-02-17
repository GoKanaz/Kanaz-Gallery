package com.gokanaz.gallery.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gokanaz.gallery.R
import com.gokanaz.gallery.adapters.MediaPagerAdapter
import com.gokanaz.gallery.databinding.ActivityPhotoDetailBinding
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.models.MediaType
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.utils.FileUtils

class PhotoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoDetailBinding
    private lateinit var adapter: MediaPagerAdapter
    private var mediaList: List<MediaModel> = emptyList()
    private var currentPosition = 0
    private val favoriteIds = mutableSetOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        setupListeners()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
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
        binding.btnFavorite.setOnClickListener { toggleFavorite() }
        binding.btnShare.setOnClickListener { shareCurrentMedia() }
        binding.btnDelete.setOnClickListener { deleteCurrentMedia() }
        binding.btnInfo.setOnClickListener { showMediaInfo() }
    }

    private fun loadData() {
        mediaList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(Constants.EXTRA_MEDIA_LIST, MediaModel::class.java)
                ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(Constants.EXTRA_MEDIA_LIST) ?: arrayListOf()
        }

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
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        val isFavorite = favoriteIds.contains(currentMedia.id)
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    private fun toggleFavorite() {
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        if (favoriteIds.contains(currentMedia.id)) {
            favoriteIds.remove(currentMedia.id)
        } else {
            favoriteIds.add(currentMedia.id)
        }
        updateFavoriteButton()
    }

    private fun shareCurrentMedia() {
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, currentMedia.uri)
            type = if (currentMedia.type == MediaType.PHOTO) "image/*" else "video/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun deleteCurrentMedia() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.ok) { _, _ ->
                val currentMedia = mediaList.getOrNull(currentPosition) ?: return@setPositiveButton
                if (FileUtils.deleteFile(this, currentMedia.uri)) {
                    finish()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showMediaInfo() {
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        val info = buildString {
            append("Name: ${currentMedia.name}\n")
            append("Path: ${currentMedia.path}\n")
            append("Size: ${currentMedia.getFormattedSize()}\n")
            append("Date: ${android.text.format.DateFormat.format("dd MMM yyyy HH:mm", currentMedia.dateAdded * 1000)}\n")
            append("Resolution: ${currentMedia.getResolution()}")
            if (currentMedia.type == MediaType.VIDEO) {
                append("\nDuration: ${currentMedia.getFormattedDuration()}")
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Media Info")
            .setMessage(info)
            .setPositiveButton("OK", null)
            .show()
    }
}
