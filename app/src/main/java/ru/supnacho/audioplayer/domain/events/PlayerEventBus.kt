package ru.supnacho.audioplayer.domain.events

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class PlayerEventBus: PlayerEventsProvider,
    PlayerEventsPublisher {
    private val bus = BehaviorSubject.create<PlayerEvents>()
    override fun provide(): Observable<PlayerEvents> = bus.hide()

    override fun publish(event: PlayerEvents) {
        bus.onNext(event)
    }
}