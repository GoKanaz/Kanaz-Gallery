package com.gokanaz.gallery.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gokanaz.gallery.R
import com.gokanaz.gallery.models.MediaModel

class AlbumAdapter(
    private var albums: Map<String, List<MediaModel>>,
    private val onAlbumClick: (String, List<MediaModel>) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    
    private val albumList = albums.toList()
    
    fun setAlbums(newAlbums: Map<String, List<MediaModel>>) {
        albums = newAlbums
        albumList.clear()
        albumList.addAll(albums.toList())
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (albumName, mediaList) = albumList[position]
        holder.bind(albumName, mediaList)
    }
    
    override fun getItemCount(): Int = albumList.size
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.albumThumbnail)
        private val albumName: TextView = itemView.findViewById(R.id.albumName)
        private val itemCount: TextView = itemView.findViewById(R.id.itemCount)
        private val dateInfo: TextView = itemView.findViewById(R.id.dateInfo)
        
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val (albumName, mediaList) = albumList[position]
                    onAlbumClick(albumName, mediaList)
                }
            }
        }
        
        fun bind(albumName: String, mediaList: List<MediaModel>) {
            this.albumName.text = albumName
            itemCount.text = "${mediaList.size} items"
            
            // Get latest media date
            val latestMedia = mediaList.maxByOrNull { it.dateAdded }
            latestMedia?.let {
                dateInfo.text = android.text.format.DateFormat.format("MMM dd, yyyy", it.dateAdded * 1000)
            }
            
            // Load thumbnail (first image in album)
            val firstMedia = mediaList.firstOrNull()
            if (firstMedia != null) {
                Glide.with(itemView.context)
                    .load(firstMedia.uri)
                    .centerCrop()
                    .placeholder(R.color.gray_300)
                    .error(R.drawable.ic_album)
                    .into(thumbnail)
            } else {
                thumbnail.setImageResource(R.drawable.ic_album)
            }
        }
    }
}
