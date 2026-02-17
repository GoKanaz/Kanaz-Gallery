package com.gokanaz.gallery.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gokanaz.gallery.models.MediaModel
import com.gokanaz.gallery.models.MediaType
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.utils.MediaLoader
import com.gokanaz.gallery.utils.PermissionsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    private val _allMedia = MutableLiveData<List<MediaModel>>(emptyList())
    val allMedia: LiveData<List<MediaModel>> = _allMedia

    private val _filteredMedia = MutableLiveData<List<MediaModel>>(emptyList())
    val filteredMedia: LiveData<List<MediaModel>> = _filteredMedia

    private val _albums = MutableLiveData<Map<String, List<MediaModel>>>(emptyMap())
    val albums: LiveData<Map<String, List<MediaModel>>> = _albums

    private val _favorites = MutableLiveData<List<MediaModel>>(emptyList())
    val favorites: LiveData<List<MediaModel>> = _favorites

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filterType = MutableLiveData(Constants.FilterType.ALL)
    val filterType: LiveData<Constants.FilterType> = _filterType

    private val _selectedItems = MutableLiveData<Set<MediaModel>>(emptySet())
    val selectedItems: LiveData<Set<MediaModel>> = _selectedItems

    private val favoriteIds = mutableSetOf<Long>()
    private var albumFilter: String? = null

    init {
        loadFavoritesFromPrefs()
        loadMedia()
    }

    fun loadMedia() {
        if (!PermissionsHelper.hasStoragePermission(context)) return

        viewModelScope.launch {
            _isLoading.value = true
            val media = withContext(Dispatchers.IO) {
                MediaLoader.loadAllMedia(context)
            }
            _allMedia.value = media
            _albums.value = media.groupBy { it.bucketName }
            applyFilterAndSearch()
            _isLoading.value = false
        }
    }

    fun setAlbumFilter(album: String?) {
        albumFilter = album
        applyFilterAndSearch()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilterAndSearch()
    }

    fun setFilterType(type: Constants.FilterType) {
        _filterType.value = type
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val all = _allMedia.value ?: emptyList()
        val query = _searchQuery.value ?: ""
        val filter = _filterType.value ?: Constants.FilterType.ALL

        var filtered = all

        if (albumFilter != null) {
            filtered = filtered.filter { it.bucketName == albumFilter }
        }

        filtered = when (filter) {
            Constants.FilterType.PHOTOS -> filtered.filter { it.type == MediaType.PHOTO }
            Constants.FilterType.VIDEOS -> filtered.filter { it.type == MediaType.VIDEO }
            Constants.FilterType.FAVORITES -> filtered.filter { favoriteIds.contains(it.id) }
            else -> filtered
        }

        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        _filteredMedia.value = filtered
    }

    fun toggleFavorite(media: MediaModel) {
        if (favoriteIds.contains(media.id)) {
            favoriteIds.remove(media.id)
        } else {
            favoriteIds.add(media.id)
        }
        saveFavoritesToPrefs()
        updateFavoritesList()
        applyFilterAndSearch()
    }

    fun isFavorite(mediaId: Long): Boolean = favoriteIds.contains(mediaId)

    private fun loadFavoritesFromPrefs() {
        val saved = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        favoriteIds.clear()
        favoriteIds.addAll(saved.map { it.toLong() })
        updateFavoritesList()
    }

    private fun saveFavoritesToPrefs() {
        prefs.edit().putStringSet("favorites", favoriteIds.map { it.toString() }.toSet()).apply()
    }

    private fun updateFavoritesList() {
        val all = _allMedia.value ?: emptyList()
        _favorites.value = all.filter { favoriteIds.contains(it.id) }
    }

    fun toggleSelection(media: MediaModel) {
        val current = _selectedItems.value?.toMutableSet() ?: mutableSetOf()
        if (current.contains(media)) current.remove(media)
        else if (current.size < Constants.MAX_SELECTION) current.add(media)
        _selectedItems.value = current
    }

    fun selectAll() {
        _selectedItems.value = (_filteredMedia.value ?: emptyList()).take(Constants.MAX_SELECTION).toSet()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun removeFromSelection(media: MediaModel) {
        val current = _selectedItems.value?.toMutableSet() ?: return
        current.remove(media)
        _selectedItems.value = current
    }

    fun deleteSelected() {
        clearSelection()
        loadMedia()
    }

    fun getThemeMode(): Constants.ThemeMode {
        val ordinal = prefs.getInt(Constants.KEY_THEME, Constants.ThemeMode.SYSTEM.ordinal)
        return Constants.ThemeMode.values()[ordinal]
    }

    fun setThemeMode(mode: Constants.ThemeMode) {
        prefs.edit().putInt(Constants.KEY_THEME, mode.ordinal).apply()
    }

    fun getSortType(): Constants.SortType {
        val ordinal = prefs.getInt(Constants.KEY_SORT_TYPE, Constants.SortType.DATE.ordinal)
        return Constants.SortType.values()[ordinal]
    }

    fun setSortType(type: Constants.SortType) {
        prefs.edit().putInt(Constants.KEY_SORT_TYPE, type.ordinal).apply()
    }

    fun getSortOrder(): Constants.SortOrder {
        val ordinal = prefs.getInt(Constants.KEY_SORT_ORDER, Constants.SortOrder.DESCENDING.ordinal)
        return Constants.SortOrder.values()[ordinal]
    }

    fun setSortOrder(order: Constants.SortOrder) {
        prefs.edit().putInt(Constants.KEY_SORT_ORDER, order.ordinal).apply()
    }
}
