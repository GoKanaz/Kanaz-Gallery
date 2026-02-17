package com.gokanaz.gallery.adapters

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gokanaz.gallery.activities.VideoPlayerActivity
import com.gokanaz.gallery.fragments.FullscreenImageFragment
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.models.MediaType
import com.gokanaz.gallery.utils.Constants

class MediaPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    private val mediaList: List<MediaModel>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = mediaList.size

    override fun createFragment(position: Int): Fragment {
        val media = mediaList[position]
        return when (media.type) {
            MediaType.PHOTO -> FullscreenImageFragment.newInstance(media)
            MediaType.VIDEO -> VideoThumbnailFragment.newInstance(media) {
                val intent = Intent(fragmentActivity, VideoPlayerActivity::class.java).apply {
                    putExtra(Constants.EXTRA_MEDIA_URI, media.uri)
                    putExtra(Constants.EXTRA_MEDIA_ID, media.id)
                }
                fragmentActivity.startActivity(intent)
            }
        }
    }
}
