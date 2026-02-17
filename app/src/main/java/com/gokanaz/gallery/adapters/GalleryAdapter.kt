package com.gokanaz.gallery.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gokanaz.gallery.R
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.models.MediaType
import com.gokanaz.gallery.utils.DateUtils

class GalleryAdapter(
    private var items: List<MediaModel>,
    private val onItemClick: (MediaModel) -> Unit,
    private val onItemLongClick: (MediaModel) -> Boolean,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<MediaModel>()

    fun setItems(newItems: List<MediaModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) selectedItems.clear()
        notifyDataSetChanged()
    }

    fun isSelectionMode(): Boolean = isSelectionMode

    fun getSelectedItems(): Set<MediaModel> = selectedItems.toSet()

    fun selectAll() {
        selectedItems.clear()
        selectedItems.addAll(items)
        notifyDataSetChanged()
        onSelectionChanged.invoke()
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectionChanged.invoke()
    }

    fun toggleSelection(media: MediaModel) {
        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }
        notifyDataSetChanged()
        onSelectionChanged.invoke()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        private val videoIndicator: ImageView = itemView.findViewById(R.id.videoIndicator)
        private val duration: TextView = itemView.findViewById(R.id.duration)
        private val favoriteIndicator: ImageView = itemView.findViewById(R.id.favoriteIndicator)
        private val selectionCheckbox: CheckBox = itemView.findViewById(R.id.selectionCheckbox)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val media = items[pos]
                    if (isSelectionMode) toggleSelection(media) else onItemClick(media)
                }
            }

            itemView.setOnLongClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemLongClick(items[pos])
                } else false
            }

            selectionCheckbox.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) toggleSelection(items[pos])
            }
        }

        fun bind(media: MediaModel) {
            Glide.with(itemView.context)
                .load(media.uri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .placeholder(R.color.gray_300)
                .error(R.color.gray_400)
                .into(thumbnail)

            if (media.type == MediaType.VIDEO) {
                videoIndicator.visibility = View.VISIBLE
                duration.visibility = View.VISIBLE
                duration.text = DateUtils.formatDuration(media.duration)
            } else {
                videoIndicator.visibility = View.GONE
                duration.visibility = View.GONE
            }

            favoriteIndicator.visibility = if (media.isFavorite) View.VISIBLE else View.GONE

            if (isSelectionMode) {
                selectionCheckbox.visibility = View.VISIBLE
                selectionCheckbox.isChecked = selectedItems.contains(media)
            } else {
                selectionCheckbox.visibility = View.GONE
            }
        }
    }
}
