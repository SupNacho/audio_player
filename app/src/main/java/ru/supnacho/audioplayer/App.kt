package ru.supnacho.audioplayer

import android.app.Application
import io.reactivex.plugins.RxJavaPlugins
import ru.supnacho.audioplayer.di.DaggerPlayerDependenciesComponent
import ru.supnacho.audioplayer.di.PlayerDiHolder
import ru.supnacho.audioplayer.utils.safeLog

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerDiHolder.playerDependencies = DaggerPlayerDependenciesComponent.create()
        RxJavaPlugins.setErrorHandler { safeLog("RX_ERROR", it.message ?: "") }
    }
}