package ru.supnacho.audioplayer.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import ru.supnacho.audioplayer.domain.events.PlayerServiceEvent
import ru.supnacho.audioplayer.domain.events.PlayerUiEvent
import ru.supnacho.audioplayer.domain.model.FileModel
import ru.supnacho.audioplayer.domain.player.PlayListHandler
import ru.supnacho.audioplayer.screen.events.ScreenEvents
import ru.supnacho.audioplayer.utils.LiveEvent
import ru.supnacho.audioplayer.utils.subscribeAndTrack
import ru.supnacho.audioplayer.utils.toFile
import java.io.File
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val playerEventsPublisher: PlayerEventsPublisher,
    playerEventsProvider: PlayerEventsProvider,
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
            .subscribeOn(Schedulers.computation())
            .subscribeAndTrack(
                subscriptionsHolder = disposables,
                onSuccess = {
                    when (it) {
                        is PlayerServiceEvent -> {
                            when (it) {
                                is PlayerServiceEvent.OnPlayPressed -> onPlayPressed()
                                is PlayerServiceEvent.OnPausePressed -> onPausePressed()
                                is PlayerServiceEvent.OnNextPressed -> onNextPressedByService(it)
                                is PlayerServiceEvent.OnStopPressed -> onStopPressedByService()
                            }
                        }
                        is PlayerUiEvent -> { }
                    }
                },
                onError = { viewStateEvents.postValue(ScreenEvents.ReplayingError) }
            )
    }

    private fun onStopPressedByService() {
        _viewState.postValue(_viewState.value?.copy(controlState = ScreenViewState.ControlState.STOPPED))
    }

    private fun onNextPressedByService(nextItem: PlayerServiceEvent.OnNextPressed) {
        _viewState.postValue(
            _viewState.value?.let { svs ->
                val newList = svs.files.map { item ->
                    FileModel(
                        item.file,
                        item.file == nextItem.currentTrack.file
                    )
                }
                svs.copy(
                    files = newList,
                    currentFile = nextItem.currentTrack.file,
                    controlState = ScreenViewState.ControlState.PLAYING
                )
            })
    }

    fun onPlaySelected(file: FileModel) {
        playerEventsPublisher.publish(PlayerUiEvent.OnPlaySelected(file))
    }

    fun onNextPressed() {
        playerEventsPublisher.publish(PlayerUiEvent.OnNextPressed)
    }

    fun onPlayPressed() {
        _viewState.postValue(_viewState.value?.copy(controlState = ScreenViewState.ControlState.PLAYING))
        playerEventsPublisher.publish(PlayerUiEvent.OnPlayPressed)
    }

    fun onPausePressed() {
        _viewState.postValue(_viewState.value?.copy(controlState = ScreenViewState.ControlState.PAUSED))
        playerEventsPublisher.publish(PlayerUiEvent.OnPausePressed)
    }

    fun onStopPressed() {
        playerEventsPublisher.publish(PlayerUiEvent.OnStopPressed)
    }

    fun onRefresh() {
        viewState.value?.let {
            getFilesList(it.directoryPath, it.currentFile)
        } ?: run { viewStateEvents.value = ScreenEvents.noDir }
    }

    fun getFilesList(path: String?) {
        path?.run {
            val selectedFile = path.toFile()
            val directory = selectedFile.parent?.toFile()
            getFilesList(directory, selectedFile)
        } ?: run { viewStateEvents.value = ScreenEvents.noDir }
    }

    private fun getFilesList(directory: File?, selectedFile: File) {
        directory?.run {
            Single.create<List<FileModel>> { emitter ->
                    val list = listFiles()?.map { FileModel(it, selectedFile == it) } ?: emptyList()
                    playListHandler.run {
                        playList = list
                        currentTrack = list.find { it.isCurrent }
                    }
                    emitter.onSuccess(list)
                }
                .subscribeOn(Schedulers.computation())
                .subscribeAndTrack(
                    subscriptionsHolder = disposables,
                    onSuccess = { list ->
                        _viewState.postValue(
                            _viewState.value?.copy(
                                directoryPath = this,
                                currentFile = selectedFile,
                                files = list
                            )
                        )
                    },
                    onError = { viewStateEvents.postValue(ScreenEvents.noFiles) }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}