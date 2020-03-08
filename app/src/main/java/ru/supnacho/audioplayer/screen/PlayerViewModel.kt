package ru.supnacho.audioplayer.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.supnacho.audioplayer.domain.player.PlayListHandler
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import ru.supnacho.audioplayer.domain.events.PlayerServiceEvent
import ru.supnacho.audioplayer.domain.events.PlayerUiEvent
import ru.supnacho.audioplayer.domain.model.FileModel
import ru.supnacho.audioplayer.screen.events.ScreenEvents
import ru.supnacho.audioplayer.utils.LiveEvent
import ru.supnacho.audioplayer.utils.safeLog
import ru.supnacho.audioplayer.utils.subscribeAndTrack
import ru.supnacho.audioplayer.utils.toFile
import java.io.File
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val playerEventsPublisher: PlayerEventsPublisher,
    private val playerEventsProvider: PlayerEventsProvider,
    private val playListHandler: PlayListHandler
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
        playerEventsProvider.provide()
            .subscribeAndTrack(
                subscriptionsHolder = disposables,
                onSuccess = {
                    when (it) {
                        is PlayerServiceEvent -> {
                            when (it) {
                                is PlayerServiceEvent.OnPlayPressed -> onPlayPressed()
                                is PlayerServiceEvent.OnPausePressed -> onPausePressed()
                                is PlayerServiceEvent.OnNextPressed -> {
                                    _viewState.value =
                                        _viewState.value?.let { svs ->
                                            val newList = svs.files.map { item ->
                                                FileModel(
                                                    item.file,
                                                    item.file == it.currentTrack.file
                                                )
                                            }
                                            svs.copy(
                                                files = newList,
                                                currentFile = it.currentTrack.file
                                            )
                                        }

                                }
                                is PlayerServiceEvent.OnStopPressed ->
                                    _viewState.value =
                                        _viewState.value?.copy(controlState = ScreenViewState.ControlState.STOPPED)
                            }
                        }
                        is PlayerUiEvent -> {
                        }
                    }
                },
                onError = { safeLog("ON ERROR", it.message!!) }
            )
    }

    fun onNextPressed() {
        playerEventsPublisher.publish(PlayerUiEvent.OnNextPressed)
    }

    fun onPlayPressed() {
        _viewState.value =
            _viewState.value?.copy(controlState = ScreenViewState.ControlState.PLAYING)
        playerEventsPublisher.publish(PlayerUiEvent.OnPlayPressed)
    }

    fun onPausePressed() {
        _viewState.value =
            _viewState.value?.copy(controlState = ScreenViewState.ControlState.PAUSED)
        playerEventsPublisher.publish(PlayerUiEvent.OnPausePressed)
    }

    fun onStopPressed() {
        playerEventsPublisher.publish(PlayerUiEvent.OnStopPressed)
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
                    _viewState.value?.copy(
                        directoryPath = this,
                        currentFile = selectedFile,
                        files = list
                    )
                )
                playListHandler.run {
                    playList = list
                    currentTrack = list.find { it.isCurrent }
                }
            }
        } ?: run { viewStateEvents.value = ScreenEvents.noDir }
    }
}