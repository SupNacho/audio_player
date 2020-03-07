package ru.supnacho.audioplayer.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.supnacho.audioplayer.domain.events.PlayerEvents
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import ru.supnacho.audioplayer.domain.model.FileModel
import ru.supnacho.audioplayer.screen.events.ScreenEvents
import ru.supnacho.audioplayer.utils.LiveEvent
import ru.supnacho.audioplayer.utils.toFile
import java.io.File
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val playerEventsPublisher: PlayerEventsPublisher,
    private val playerEventsProvider: PlayerEventsProvider
) : ViewModel() {
    private val _viewState = MutableLiveData<ScreenViewState>()
    val viewState: LiveData<ScreenViewState>
        get() = _viewState
    val viewStateEvents = LiveEvent<ScreenEvents>()
    private val disposables = CompositeDisposable()

    init {
        _viewState.value = ScreenViewState(
            directoryPath = File(""),
            currentFile = File(""),
            controlState = ScreenViewState.ControlState.STOPPED
        )
//        playerEventsProvider.provide()
////            .subscribe {
////                _viewState.value =
////            }
    }

    fun onNextPressed() {
        playerEventsPublisher.publish(PlayerEvents.Next)
    }

    fun onPlayPressed(){
        _viewState.value =
            _viewState.value?.copy(controlState = ScreenViewState.ControlState.PLAYING)
    }

    fun onPausePressed(){
        _viewState.value =
            _viewState.value?.copy(controlState = ScreenViewState.ControlState.PAUSED)
    }

    fun onStopPressed(){
        _viewState.value =
            _viewState.value?.copy(controlState = ScreenViewState.ControlState.STOPPED)
    }

    fun onRefresh() {

    }

    fun getFilesList(path: String?) {
        path?.run {
            val selectedFile = path.toFile()
            val directory = selectedFile.parent?.toFile()
            directory?.run {
                val list = listFiles()?.map { FileModel(it, selectedFile == it) } ?: emptyList()
                _viewState.postValue(
                    _viewState.value?.copy(directoryPath = this, currentFile = selectedFile, files = list)
                )
            }
        } ?: run { viewStateEvents.value = ScreenEvents.noDir }
    }
}