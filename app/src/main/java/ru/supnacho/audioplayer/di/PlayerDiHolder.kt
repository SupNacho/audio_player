package ru.supnacho.audioplayer.di

object PlayerDiHolder {
    lateinit var playerDependencies: PlayerDependencies
}

val playerDependencies: PlayerDependencies
    get() = PlayerDiHolder.playerDependencies