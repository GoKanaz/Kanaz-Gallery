package com.gokanaz.gallery.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gokanaz.gallery.R
import com.gokanaz.gallery.activities.PhotoDetailActivity
import com.gokanaz.gallery.activities.SlideShowActivity
import com.gokanaz.gallery.adapters.GalleryAdapter
import com.gokanaz.gallery.databinding.FragmentGalleryBinding
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.utils.PermissionsHelper
import com.gokanaz.gallery.viewmodels.GalleryViewModel

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()

    private lateinit var adapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        setupObservers()
        setupSearchView()
        checkPermissions()
    }

    private fun setupRecyclerView() {
        adapter = GalleryAdapter(
            items = emptyList(),
            onItemClick = { media -> openMediaDetail(media) },
            onItemLongClick = { media ->
                enableSelectionMode()
                adapter.toggleSelection(media)
                true
            },
            onSelectionChanged = { updateSelectionUI() }
        )

        binding.galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), Constants.GRID_SPAN_COUNT)
        binding.galleryRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabSlideshow.setOnClickListener { startSlideshow() }

        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.setFilterType(Constants.FilterType.ALL)
        }
        binding.chipPhotos.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.setFilterType(Constants.FilterType.PHOTOS)
        }
        binding.chipVideos.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.setFilterType(Constants.FilterType.VIDEOS)
        }
        binding.chipFavorites.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.setFilterType(Constants.FilterType.FAVORITES)
        }

        binding.btnShare.setOnClickListener { shareSelected() }
        binding.btnDelete.setOnClickListener { deleteSelected() }
    }

    private fun setupObservers() {
        viewModel.filteredMedia.observe(viewLifecycleOwner) { mediaList ->
            adapter.setItems(mediaList)
            updateEmptyState(mediaList.isEmpty())
            updateFabVisibility()
        }
    }

    private fun setupSearchView() {
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
        })
    }

    private fun checkPermissions() {
        if (!PermissionsHelper.hasStoragePermission(requireContext())) {
            @Suppress("DEPRECATION")
            requestPermissions(PermissionsHelper.getRequiredPermissions(), Constants.REQUEST_CODE_PERMISSION)
        } else {
            viewModel.loadMedia()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSION) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.loadMedia()
            }
        }
    }

    private fun openMediaDetail(media: MediaModel) {
        val mediaList = viewModel.filteredMedia.value ?: return
        val position = mediaList.indexOf(media)
        val intent = Intent(requireContext(), PhotoDetailActivity::class.java).apply {
            putParcelableArrayListExtra(Constants.EXTRA_MEDIA_LIST, ArrayList(mediaList))
            putExtra(Constants.EXTRA_POSITION, if (position >= 0) position else 0)
        }
        startActivity(intent)
    }

    private fun startSlideshow() {
        val mediaList = viewModel.filteredMedia.value ?: return
        if (mediaList.isNotEmpty()) {
            val intent = Intent(requireContext(), SlideShowActivity::class.java).apply {
                putParcelableArrayListExtra(Constants.EXTRA_MEDIA_LIST, ArrayList(mediaList))
            }
            startActivity(intent)
        }
    }

    private fun enableSelectionMode() {
        adapter.setSelectionMode(true)
        binding.selectionBar.visibility = View.VISIBLE
        binding.fabSlideshow.hide()
        requireActivity().invalidateOptionsMenu()
    }

    private fun disableSelectionMode() {
        adapter.setSelectionMode(false)
        binding.selectionBar.visibility = View.GONE
        binding.fabSlideshow.show()
        requireActivity().invalidateOptionsMenu()
        viewModel.clearSelection()
    }

    private fun updateSelectionUI() {
        val selectedCount = adapter.getSelectedItems().size
        if (selectedCount == 0) {
            disableSelectionMode()
        } else {
            binding.selectedCount.text = getString(R.string.items_selected, selectedCount)
        }
    }

    private fun shareSelected() {
        val selected = adapter.getSelectedItems()
    }

    private fun deleteSelected() {
        val selected = adapter.getSelectedItems()
        if (selected.isEmpty()) return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.deleteSelected()
                disableSelectionMode()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun updateFabVisibility() {
        val mediaList = viewModel.filteredMedia.value
        binding.fabSlideshow.visibility = if (mediaList.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
