package ru.supnacho.audioplayer.data.storage

import android.content.Context
import javax.inject.Inject

interface LocalStorageBoundary {
    fun saveLastState(filePath: String)
    fun restoreLastState(): String?
}

class LocalStorageBoundaryImpl @Inject constructor(context: Context):
    LocalStorageBoundary {
    private val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)

    override fun saveLastState(filePath: String) {
        sharedPreferences.edit().putString(SP_LF, filePath)
            .apply()
    }

    override fun restoreLastState(): String? {
        return sharedPreferences.getString(SP_LF, null)
    }

    private companion object {
        const val SP_FILE_NAME = "AudioPlayerSP"
        const val SP_LF = "LastFile"
    }
}