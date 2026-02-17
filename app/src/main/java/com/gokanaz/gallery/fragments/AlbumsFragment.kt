package com.gokanaz.gallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokanaz.gallery.R
import com.gokanaz.gallery.adapters.AlbumAdapter
import com.gokanaz.gallery.databinding.FragmentAlbumsBinding
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.viewmodels.GalleryViewModel

class AlbumsFragment : Fragment() {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()

    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = AlbumAdapter(
            albums = emptyMap(),
            onAlbumClick = { albumName, mediaList ->
                val fragment = GalleryFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.EXTRA_ALBUM_NAME, albumName)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.albumsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.albumsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.setAlbums(albums)
            updateEmptyState(albums.isEmpty())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
