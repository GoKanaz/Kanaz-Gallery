package com.gokanaz.gallery.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gokanaz.gallery.databinding.FragmentFullscreenImageBinding
import com.gokanaz.gallery.models.MediaModel

class FullscreenImageFragment : Fragment() {

    private var _binding: FragmentFullscreenImageBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_MEDIA = "media"

        fun newInstance(media: MediaModel): FullscreenImageFragment {
            return FullscreenImageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_MEDIA, media)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullscreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val media = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_MEDIA, MediaModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_MEDIA)
        }

        media?.let { loadImage(it) }
    }

    private fun loadImage(media: MediaModel) {
        Glide.with(this)
            .load(media.uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.zoomImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
