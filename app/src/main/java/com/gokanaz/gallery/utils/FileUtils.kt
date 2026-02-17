package com.gokanaz.gallery.utils

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.activity.result.IntentSenderRequest
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun getMimeType(path: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    fun isImageFile(path: String): Boolean {
        return getMimeType(path)?.startsWith("image/") == true
    }

    fun isVideoFile(path: String): Boolean {
        return getMimeType(path)?.startsWith("video/") == true
    }

    fun deleteFile(context: Context, uri: Uri): Boolean {
        return try {
            val rows = context.contentResolver.delete(uri, null, null)
            rows > 0
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val recoverableException = e as? RecoverableSecurityException
                    if (recoverableException != null) {
                        return false
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            try {
                val path = getPathFromUri(context, uri)
                if (path != null) {
                    val file = File(path)
                    if (file.exists()) file.delete() else false
                } else false
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteFiles(context: Context, uris: List<Uri>): Boolean {
        return uris.all { deleteFile(context, it) }
    }

    fun getFileSize(path: String): Long {
        return try { File(path).length() } catch (e: Exception) { 0 }
    }

    fun getFileName(path: String): String = File(path).name

    fun getFileExtension(path: String): String = File(path).extension

    fun getBucketNameFromPath(path: String): String {
        return File(path).parentFile?.name ?: "Unknown"
    }

    fun getBucketIdFromPath(path: String): String {
        return File(path).parentFile?.absolutePath?.hashCode()?.toString() ?: "0"
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        return try {
            val projection = arrayOf(android.provider.MediaStore.MediaColumns.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DATA)
                    cursor.getString(idx)
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun createFileFromUri(context: Context, uri: Uri, fileName: String): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { inputStream.copyTo(it) }
            inputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
