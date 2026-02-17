package com.gokanaz.gallery.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    
    fun getMimeType(path: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    
    fun isImageFile(path: String): Boolean {
        val mimeType = getMimeType(path)
        return mimeType?.startsWith("image/") == true
    }
    
    fun isVideoFile(path: String): Boolean {
        val mimeType = getMimeType(path)
        return mimeType?.startsWith("video/") == true
    }
    
    fun deleteFile(context: Context, uri: Uri): Boolean {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun deleteFiles(context: Context, uris: List<Uri>): Boolean {
        var success = true
        for (uri in uris) {
            if (!deleteFile(context, uri)) {
                success = false
            }
        }
        return success
    }
    
    fun getFileSize(path: String): Long {
        return try {
            File(path).length()
        } catch (e: Exception) {
            0
        }
    }
    
    fun createFileFromUri(context: Context, uri: Uri, fileName: String): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getFileName(path: String): String {
        return File(path).name
    }
    
    fun getFileExtension(path: String): String {
        return File(path).extension
    }
    
    fun getBucketNameFromPath(path: String): String {
        val parent = File(path).parentFile
        return parent?.name ?: "Unknown"
    }
    
    fun getBucketIdFromPath(path: String): String {
        val parent = File(path).parentFile
        return parent?.absolutePath?.hashCode()?.toString() ?: "0"
    }
}
