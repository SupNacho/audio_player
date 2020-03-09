package ru.supnacho.audioplayer.domain.events

import ru.supnacho.audioplayer.domain.model.FileModel

sealed class PlayerEvents

sealed class PlayerServiceEvent : PlayerEvents() {
    data class OnStateUpdate(val currentTrack: FileModel)
    data class OnError(val error: Throwable)
    object OnPlayPressed : PlayerServiceEvent()
    object OnPausePressed : PlayerServiceEvent()
    data class OnNextPressed(val currentTrack: FileModel) : PlayerServiceEvent()
    object OnStopPressed : PlayerServiceEvent()
}

sealed class PlayerUiEvent : PlayerEvents() {
    object OnPlayPressed : PlayerUiEvent()
    object OnPausePressed : PlayerUiEvent()
    object OnNextPressed : PlayerUiEvent()
    object OnStopPressed : PlayerUiEvent()
    data class OnPlaySelected(val file: FileModel) : PlayerUiEvent()
}