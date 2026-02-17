package com.gokanaz.gallery.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val fullDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    fun formatFullDate(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp * 1000))
    }
    
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp * 1000))
    }
    
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp * 1000))
    }
    
    fun formatMonthYear(timestamp: Long): String {
        return monthYearFormat.format(Date(timestamp * 1000))
    }
    
    fun getRelativeTimeSpan(timestamp: Long): String {
        val now = System.currentTimeMillis() / 1000
        val diff = now - timestamp
        
        return when {
            diff < 60 -> "Just now"
            diff < 3600 -> "${diff / 60} minutes ago"
            diff < 86400 -> "${diff / 3600} hours ago"
            diff < 604800 -> "${diff / 86400} days ago"
            diff < 2592000 -> "${diff / 604800} weeks ago"
            diff < 31536000 -> "${diff / 2592000} months ago"
            else -> "${diff / 31536000} years ago"
        }
    }
    
    fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
}
