package ru.supnacho.audioplayer.domain

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PlayerEventBus: PlayerEventsProvider, PlayerEventsPublisher {
    private val bus = PublishSubject.create<PlayerEvents>()
    override fun provide(): Observable<PlayerEvents> = bus.hide()

    override fun publish(event: PlayerEvents) {
        bus.onNext(event)
    }
}