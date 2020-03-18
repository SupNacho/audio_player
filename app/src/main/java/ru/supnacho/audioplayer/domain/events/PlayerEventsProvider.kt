package ru.supnacho.audioplayer.domain.events

import io.reactivex.Observable
import ru.supnacho.audioplayer.domain.events.PlayerEvents

interface PlayerEventsProvider {
    fun provide(): Observable<PlayerEvents>
}