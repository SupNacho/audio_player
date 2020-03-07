package ru.supnacho.audioplayer.screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.supnacho.audioplayer.domain.events.PlayerEvents
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val playerEventsPublisher: PlayerEventsPublisher,
    private val playerEventsProvider: PlayerEventsProvider
): ViewModel() {
    val liveData = MutableLiveData<String>()
    init {
        playerEventsProvider.provide()
            .subscribe {
                liveData.value = it.toString()
            }
    }

    fun onNext(){
        playerEventsPublisher.publish(PlayerEvents.Next)
    }
}