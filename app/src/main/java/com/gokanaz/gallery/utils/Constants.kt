package com.gokanaz.gallery.utils

object Constants {
    const val DATABASE_NAME = "kanaz_gallery.db"
    const val PREFS_NAME = "kanaz_gallery_prefs"
    const val KEY_THEME = "theme_mode"
    const val KEY_SORT_ORDER = "sort_order"
    const val KEY_SORT_TYPE = "sort_type"

    const val GRID_SPAN_COUNT = 3
    const val SLIDESHOW_DEFAULT_DELAY = 3000L

    const val REQUEST_CODE_PERMISSION = 100
    const val REQUEST_CODE_VIDEO_CAPTURE = 101
    const val REQUEST_CODE_IMAGE_CAPTURE = 102

    const val MAX_SELECTION = 50

    const val EXTRA_MEDIA_ID = "extra_media_id"
    const val EXTRA_MEDIA_URI = "extra_media_uri"
    const val EXTRA_MEDIA_LIST = "extra_media_list"
    const val EXTRA_POSITION = "extra_position"
    const val EXTRA_ALBUM_NAME = "extra_album_name"

    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }

    enum class SortType {
        DATE, NAME, SIZE
    }

    enum class SortOrder {
        ASCENDING, DESCENDING
    }

    enum class FilterType {
        ALL, PHOTOS, VIDEOS, FAVORITES
    }
}
