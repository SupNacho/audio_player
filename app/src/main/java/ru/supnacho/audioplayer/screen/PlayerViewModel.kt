package ru.supnacho.audioplayer.screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.schedulers.Schedulers
import ru.supnacho.audioplayer.domain.PlayerEvents
import ru.supnacho.audioplayer.domain.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.PlayerEventsPublisher
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