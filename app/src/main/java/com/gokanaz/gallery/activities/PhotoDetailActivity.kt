import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gokanaz.gallery.R
import com.gokanaz.gallery.adapters.MediaPagerAdapter
import com.gokanaz.gallery.databinding.ActivityPhotoDetailBinding
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.models.MediaType
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.viewmodels.GalleryViewModel

class PhotoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoDetailBinding
    private lateinit var viewModel: GalleryViewModel
    private lateinit var adapter: MediaPagerAdapter
    private var mediaList: List<MediaModel> = emptyList()
    private var currentPosition = 0
    private var pendingDeleteUri: Uri? = null

    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]

        deleteResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.loadMedia()
                finish()
            }
        }

        setupToolbar()
        setupViewPager()
        setupListeners()
        observeMedia()
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

    private fun observeMedia() {
        val filterOrdinal = intent.getIntExtra(Constants.EXTRA_FILTER_TYPE, 0)
        val filterType = Constants.FilterType.values()[filterOrdinal]
        val targetPosition = intent.getIntExtra(Constants.EXTRA_POSITION, 0)

        viewModel.setFilterType(filterType)

        viewModel.filteredMedia.observe(this) { list ->
            if (list.isEmpty()) return@observe
            mediaList = list

            if (!::adapter.isInitialized) {
                adapter = MediaPagerAdapter(this, mediaList)
                binding.viewPager.adapter = adapter
                binding.viewPager.setCurrentItem(targetPosition, false)
                currentPosition = targetPosition
            } else {
                adapter = MediaPagerAdapter(this, mediaList)
                binding.viewPager.adapter = adapter
            }

            updateCounter()
            updateFavoriteButton()
        }
    }

    private fun updateCounter() {
        binding.counter.text = "${currentPosition + 1}/${mediaList.size}"
    }

    private fun updateFavoriteButton() {
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        val isFavorite = viewModel.isFavorite(currentMedia.id)
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    private fun toggleFavorite() {
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        viewModel.toggleFavorite(currentMedia)
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
        val currentMedia = mediaList.getOrNull(currentPosition) ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.ok) { _, _ ->
                performDelete(currentMedia)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun performDelete(media: MediaModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val pendingIntent = MediaStore.createDeleteRequest(
                    contentResolver,
                    listOf(media.uri)
                )
                deleteResultLauncher.launch(
                    IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                contentResolver.delete(media.uri, null, null)
                viewModel.loadMedia()
                finish()
            } catch (e: android.app.RecoverableSecurityException) {
                val intentSender = e.userAction.actionIntent.intentSender
                deleteResultLauncher.launch(
                    IntentSenderRequest.Builder(intentSender).build()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                val rows = contentResolver.delete(media.uri, null, null)
                if (rows > 0) {
                    viewModel.loadMedia()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
