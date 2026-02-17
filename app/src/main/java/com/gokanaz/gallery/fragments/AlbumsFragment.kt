package com.gokanaz.gallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokanaz.gallery.R
import com.gokanaz.gallery.adapters.AlbumAdapter
import com.gokanaz.gallery.databinding.FragmentAlbumsBinding
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
            onAlbumClick = { albumName, _ ->
                val bundle = Bundle().apply {
                    putString("album_name", albumName)
                }
                findNavController().navigate(R.id.galleryFragment, bundle)
            }
        )

        binding.albumsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.albumsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.setAlbums(albums)
            binding.emptyView.visibility = if (albums.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
