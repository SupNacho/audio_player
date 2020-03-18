package ru.supnacho.audioplayer.di

import android.content.Context
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher

interface PlayerDependencies {
    val playerEventProvider: PlayerEventsProvider
    val playerEventPublisher: PlayerEventsPublisher
    val context: Context
}