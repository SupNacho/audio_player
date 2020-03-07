package ru.supnacho.audioplayer.domain.events

import java.io.File

sealed class PlayerEvents {
    data class Play(val trackPath: File): PlayerEvents()
    object Next: PlayerEvents()
    object Pause: PlayerEvents()
    object Stop: PlayerEvents()
}