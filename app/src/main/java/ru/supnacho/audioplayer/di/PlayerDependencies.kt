package ru.supnacho.audioplayer.di

import ru.supnacho.audioplayer.domain.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.PlayerEventsPublisher

interface PlayerDependencies {
    val playerEventProvider: PlayerEventsProvider
    val playerEventPublisher: PlayerEventsPublisher
}