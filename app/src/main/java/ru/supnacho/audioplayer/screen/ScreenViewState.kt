package ru.supnacho.audioplayer.screen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.supnacho.audioplayer.domain.model.FileModel
import java.io.File

@Parcelize
data class ScreenViewState (
    val directoryPath: File,
    val currentFile: File,
    val files: List<FileModel> = emptyList(),
    val controlState: ControlState
): Parcelable {
    enum class ControlState{
        PLAYING,
        PAUSED,
        STOPPED
    }
}