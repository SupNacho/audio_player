package ru.supnacho.audioplayer.screen.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.supnacho.audioplayer.di.DaggerPlayerDependenciesComponent

class ViewModelFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DaggerPlayerDependenciesComponent.create().playerViewModel as T
    }
}