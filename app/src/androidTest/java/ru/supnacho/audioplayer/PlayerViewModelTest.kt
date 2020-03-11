package ru.supnacho.audioplayer

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.MockitoAnnotations
import ru.supnacho.audioplayer.data.storage.LocalStorageBoundary
import ru.supnacho.audioplayer.domain.events.*
import ru.supnacho.audioplayer.domain.model.FileModel
import ru.supnacho.audioplayer.domain.player.PlayListHandler
import ru.supnacho.audioplayer.screen.PlayerViewModel
import ru.supnacho.audioplayer.screen.ScreenViewState
import ru.supnacho.audioplayer.screen.events.ScreenEvents
import java.io.File

@RunWith(AndroidJUnit4::class)
class PlayerViewModelTest {

    private lateinit var context: Context

    val eventBus = PlayerEventBus()
    val playerEventsPublisher: PlayerEventsPublisher = eventBus
    val playerEventsProvider: PlayerEventsProvider = eventBus

    @Mock
    val playListHandler: PlayListHandler = Mockito.mock(PlayListHandler::class.java)

    @Mock
    val localStorage: LocalStorageBoundary = Mockito.mock(LocalStorageBoundary::class.java)

    @Mock
    lateinit var observerLD: Observer<ScreenEvents>

    @Mock
    lateinit var observerVS: Observer<ScreenViewState>

    lateinit var viewModel: PlayerViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext
        viewModel = PlayerViewModel(
            playerEventsPublisher,
            playerEventsProvider,
            playListHandler,
            localStorage
        )
    }

    @Test
    fun testPlaySelected() {
        val observer = TestObserver<PlayerEvents>()
        val selectedFile = FileModel(File("test"), false)
        viewModel.onPlaySelected(selectedFile)
        playerEventsProvider.provide().subscribeWith(observer)
        observer.awaitCount(1)
        observer.assertValue(PlayerUiEvent.OnPlaySelected(selectedFile))
    }

    @Test
    fun testNextPressed() {
        val observer = TestObserver<PlayerEvents>()
        viewModel.onNextPressed()
        playerEventsProvider.provide().subscribeWith(observer)
        observer.awaitCount(1)
        observer.assertValue(PlayerUiEvent.OnNextPressed)
    }

    @Test
    fun onPlayPressed() {
        val observer = TestObserver<PlayerEvents>()
        viewModel.onPlayPressed()
        playerEventsProvider.provide().subscribeWith(observer)
        observer.awaitCount(1)
        observer.assertValue(PlayerUiEvent.OnPlayPressed)
        assertTrue(viewModel.viewState.value?.controlState == ScreenViewState.ControlState.PLAYING)
    }

    @Test
    fun onPausePressed() {
        val observer = TestObserver<PlayerEvents>()
        viewModel.onPausePressed()
        playerEventsProvider.provide().subscribeWith(observer)
        observer.awaitCount(1)
        observer.assertValue(PlayerUiEvent.OnPausePressed)
        assertTrue(viewModel.viewState.value?.controlState == ScreenViewState.ControlState.PAUSED)
    }

    @Test
    fun onStopPressed() {
        val observer = TestObserver<PlayerEvents>()
        viewModel.onStopPressed()
        playerEventsProvider.provide().subscribeWith(observer)
        observer.awaitCount(1)
        observer.assertValue(PlayerUiEvent.OnStopPressed)
    }

    @Test
    fun onRefresh() {
        Handler(Looper.getMainLooper()).post { viewModel.viewState.observeForever(observerVS) }
        Handler(Looper.getMainLooper()).post { viewModel.viewStateEvents.observeForever(observerLD) }
        viewModel.onRefresh()
        verify(observerVS).onChanged(
            ScreenViewState(
                File(""),
                File(""),
                emptyList(),
                ScreenViewState.ControlState.STOPPED
            )
        )
        verifyZeroInteractions(observerLD)
    }

    // Test events from service

    @Test
    fun onServiceStopPressed() {
        playerEventsPublisher.publish(PlayerServiceEvent.OnStopPressed)
        assertTrue(viewModel.viewState.value?.controlState == ScreenViewState.ControlState.STOPPED)
    }

    @Test
    fun testServiceNextPressed() {
        Handler(Looper.getMainLooper()).post { viewModel.viewState.observeForever(observerVS) }
        playerEventsPublisher.publish(PlayerServiceEvent.OnNextPressed(FileModel(File(""), false)))
        verify(observerVS).onChanged(
            ScreenViewState(
                File(""),
                File(""),
                emptyList(),
                ScreenViewState.ControlState.PLAYING
            )
        )

    }

    @Test
    fun onServicePlayPressed() {
        Handler(Looper.getMainLooper()).post { viewModel.viewState.observeForever(observerVS) }
        playerEventsPublisher.publish(PlayerServiceEvent.OnPlayPressed)
        verify(observerVS).onChanged(
            ScreenViewState(
                File(""),
                File(""),
                emptyList(),
                ScreenViewState.ControlState.PLAYING
            )
        )
    }

    @Test
    fun onServicePausePressed() {
        Handler(Looper.getMainLooper()).post { viewModel.viewState.observeForever(observerVS) }
        playerEventsPublisher.publish(PlayerServiceEvent.OnPausePressed)
        verify(observerVS).onChanged(
            ScreenViewState(
                File(""),
                File(""),
                emptyList(),
                ScreenViewState.ControlState.PAUSED
            )
        )
    }
}
