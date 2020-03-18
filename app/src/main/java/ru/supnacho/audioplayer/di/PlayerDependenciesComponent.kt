package ru.supnacho.audioplayer.di

import android.content.Context
import dagger.*
import ru.supnacho.audioplayer.data.storage.LocalStorageBoundary
import ru.supnacho.audioplayer.data.storage.LocalStorageBoundaryImpl
import ru.supnacho.audioplayer.domain.events.PlayerEventBus
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import ru.supnacho.audioplayer.domain.player.MediaPlayerController
import ru.supnacho.audioplayer.domain.player.MediaPlayerControllerImpl
import ru.supnacho.audioplayer.domain.player.PlayListHandler
import ru.supnacho.audioplayer.domain.player.PlayListHandlerImpl
import ru.supnacho.audioplayer.screen.PlayerViewModel
import ru.supnacho.audioplayer.service.PlayerService
import javax.inject.Singleton

@Component(modules = [PlayerDependenciesModule::class])
@Singleton
interface PlayerDependenciesComponent : PlayerDependencies {
    val playerViewModel: PlayerViewModel

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): PlayerDependenciesComponent
    }

    fun inject(service: PlayerService)
}

@Module
abstract class PlayerDependenciesModule {
    @Binds
    abstract fun bindPlayerEventsPublisher(publisher: PlayerEventBus): PlayerEventsPublisher

    @Binds
    abstract fun bindPlayerEventsProvider(publisher: PlayerEventBus): PlayerEventsProvider

    @Binds
    abstract fun bindPlayListHandler(handler: PlayListHandlerImpl): PlayListHandler

    @Binds
    abstract fun bindMediaPlayerController(controller: MediaPlayerControllerImpl): MediaPlayerController

    @Binds
    abstract fun bindLocalStorage(localStorage: LocalStorageBoundaryImpl): LocalStorageBoundary

    @Module
    companion object {
        private val playerEventBus =
            PlayerEventBus()
        private val playListHandler =
            PlayListHandlerImpl()

        @JvmStatic
        @Provides
        @Singleton
        fun providePlayerEventBus() = playerEventBus

        @JvmStatic
        @Provides
        @Singleton
        fun providePlayListHandler() = playListHandler
    }
}