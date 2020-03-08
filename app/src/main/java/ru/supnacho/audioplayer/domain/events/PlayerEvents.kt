package ru.supnacho.audioplayer.domain.events

import ru.supnacho.audioplayer.domain.model.FileModel
import ru.supnacho.audioplayer.screen.ScreenViewState
import java.io.File

sealed class PlayerEvents {
    data class Play(val trackPath: File): PlayerEvents()
    data class StateFromController(val state: ScreenViewState.ControlState, val currentTrack: FileModel): PlayerEvents()
    data class StateFromUI(val state: ScreenViewState.ControlState, val currentTrack: FileModel): PlayerEvents()
    data class UpdatePlayList(val list: List<FileModel>, val currentTrack: FileModel): PlayerEvents()
    object Next: PlayerEvents()
    object Pause: PlayerEvents()
    object Stop: PlayerEvents()
}