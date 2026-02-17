package com.gokanaz.gallery.models

import android.net.Uri
import java.util.Date

enum class MediaType {
    PHOTO, VIDEO
}

data class MediaModel(
    val id: Long,
    val name: String,
    val path: String,
    val uri: Uri,
    val type: MediaType,
    val dateAdded: Long,
    val dateModified: Long,
    val size: Long,
    val width: Int = 0,
    val height: Int = 0,
    val duration: Long = 0,
    val bucketId: String,
    val bucketName: String,
    var isFavorite: Boolean = false
) {
    fun getDateAddedAsDate(): Date = Date(dateAdded * 1000)
    fun getDateModifiedAsDate(): Date = Date(dateModified * 1000)
    
    fun getFormattedSize(): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }
    
    fun getFormattedDuration(): String {
        if (duration <= 0) return "00:00"
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        val hours = (duration / (1000 * 60 * 60))
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    fun getResolution(): String = "${width}x${height}"
}
