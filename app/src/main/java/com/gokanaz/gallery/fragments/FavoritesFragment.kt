package com.gokanaz.gallery.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.gokanaz.gallery.activities.PhotoDetailActivity
import com.gokanaz.gallery.adapters.GalleryAdapter
import com.gokanaz.gallery.databinding.FragmentFavoritesBinding
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.viewmodels.GalleryViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()

    private lateinit var adapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = GalleryAdapter(
            items = emptyList(),
            onItemClick = { media -> openMediaDetail(media) },
            onItemLongClick = { _ -> false },
            onSelectionChanged = {}
        )

        binding.favoritesRecyclerView.layoutManager = GridLayoutManager(requireContext(), Constants.GRID_SPAN_COUNT)
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.setItems(favorites)
            updateEmptyState(favorites.isEmpty())
        }
    }

    private fun openMediaDetail(media: MediaModel) {
        val intent = Intent(requireContext(), PhotoDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_MEDIA_ID, media.id)
            putExtra(Constants.EXTRA_POSITION, 0)
        }
        startActivity(intent)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
