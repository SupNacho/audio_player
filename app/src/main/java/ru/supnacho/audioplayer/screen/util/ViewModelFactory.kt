package ru.supnacho.audioplayer.screen.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.supnacho.audioplayer.di.DaggerPlayerDependenciesComponent
import ru.supnacho.audioplayer.di.playerDependencies

class ViewModelFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DaggerPlayerDependenciesComponent.factory().create(playerDependencies.context).playerViewModel as T
    }
}