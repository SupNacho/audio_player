package ru.supnacho.audioplayer.domain

interface PlayerEventsPublisher {
    fun publish(event: PlayerEvents)
}