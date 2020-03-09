package ru.supnacho.audioplayer.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class FileModel (
    val file: File,
    var isCurrent: Boolean
): Parcelable