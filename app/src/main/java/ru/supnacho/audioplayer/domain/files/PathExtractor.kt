package ru.supnacho.audioplayer.domain.files

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

object PathExtractor {
    /**
     * Get file path from URI
     *
     * @param context context of Activity
     * @param uri     uri of file
     * @return path of given URI
     */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {
        // ExternalStorageProvider
        when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                return Environment.getExternalStoragePublicDirectory(split[1]).path
            }
            // DownloadsProvider
            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            }
            // MediaProvider
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
            // MediaStore (and general)
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.lastPathSegment
                return getDataColumn(context, uri, null, null)
            }
            // File
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

    private fun isGooglePhotosUri(uri: Uri): Boolean =
        "com.google.android.apps.photos.content" == uri.authority


}