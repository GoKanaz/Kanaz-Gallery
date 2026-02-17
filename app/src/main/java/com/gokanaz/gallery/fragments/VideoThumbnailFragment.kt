package com.gokanaz.gallery.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gokanaz.gallery.R
import com.gokanaz.gallery.models.MediaModel

class VideoThumbnailFragment : Fragment() {

    private var onPlayClick: (() -> Unit)? = null

    companion object {
        private const val ARG_MEDIA = "media"

        fun newInstance(media: MediaModel, onPlayClick: () -> Unit): VideoThumbnailFragment {
            return VideoThumbnailFragment().apply {
                this.onPlayClick = onPlayClick
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
        return inflater.inflate(R.layout.fragment_video_thumbnail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val media = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_MEDIA, MediaModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_MEDIA)
        }

        val thumbnail = view.findViewById<ImageView>(R.id.videoThumbnail)
        val btnPlay = view.findViewById<ImageButton>(R.id.btnPlay)

        media?.let {
            Glide.with(this)
                .load(it.uri)
                .centerCrop()
                .into(thumbnail)
        }

        btnPlay.setOnClickListener {
            onPlayClick?.invoke()
        }

        thumbnail.setOnClickListener {
            onPlayClick?.invoke()
        }
    }
}
