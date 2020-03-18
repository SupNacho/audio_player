package ru.supnacho.audioplayer.screen.events

sealed class ScreenEvents {
    object NoFiles: ScreenEvents()
    object NoDir: ScreenEvents()
    object ReplayingError: ScreenEvents()
}