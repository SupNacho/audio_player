package ru.supnacho.audioplayer.di

import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.supnacho.audioplayer.domain.events.PlayerEventBus
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import ru.supnacho.audioplayer.screen.PlayerViewModel
import ru.supnacho.audioplayer.service.PlayerService
import javax.inject.Singleton

@Component(modules = [PlayerDependenciesModule::class])
@Singleton
interface PlayerDependenciesComponent : PlayerDependencies {
    val playerViewModel: PlayerViewModel
    fun inject(service: PlayerService)
}

@Module
abstract class PlayerDependenciesModule {
    @Binds
    abstract fun bindPlayerEventsPublisher(publisher: PlayerEventBus): PlayerEventsPublisher

    @Binds
    abstract fun bindPlayerEventsProvider(publisher: PlayerEventBus): PlayerEventsProvider

    @Module
    companion object {
        private val playerEventBus =
            PlayerEventBus()
        @JvmStatic
        @Provides
        @Singleton
        fun providePlayerEventBus() = playerEventBus
    }
}