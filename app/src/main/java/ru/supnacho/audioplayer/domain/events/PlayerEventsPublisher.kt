package ru.supnacho.audioplayer.domain.events

import ru.supnacho.audioplayer.domain.events.PlayerEvents

interface PlayerEventsPublisher {
    fun publish(event: PlayerEvents)
}