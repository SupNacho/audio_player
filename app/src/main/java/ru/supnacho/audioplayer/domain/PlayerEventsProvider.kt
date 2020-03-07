package ru.supnacho.audioplayer.domain

import io.reactivex.Observable

interface PlayerEventsProvider {
    fun provide(): Observable<PlayerEvents>
}