package ru.supnacho.audioplayer.screen.events

sealed class ScreenEvents {
    object noFiles: ScreenEvents()
    object noDir: ScreenEvents()
    object ReplayingError: ScreenEvents()
}