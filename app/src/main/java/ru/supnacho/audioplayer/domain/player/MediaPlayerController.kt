package ru.supnacho.audioplayer.domain.player

import android.media.MediaPlayer
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import ru.supnacho.audioplayer.domain.events.PlayerServiceEvent
import ru.supnacho.audioplayer.utils.safeLog
import javax.inject.Inject

interface MediaPlayerController {

    fun play()
    fun pause()
    fun next()
    fun release()
}

class MediaPlayerControllerImpl @Inject constructor(
    private val playerEventsPublisher: PlayerEventsPublisher,
    private val playListHandler: PlayListHandler
) : MediaPlayerController {
    private val mediaPlayer = MediaPlayer()
    private var isPaused = false

    init {
        mediaPlayer.setOnCompletionListener {
            playNext()
        }
    }

    private fun playNext() {
        setNextFile()
        play()
    }

    private fun setNextFile() {
        playListHandler.run {
            if (playList.isNotEmpty()) {
                val newIndex = playList.indexOf(currentTrack) + 1
                val nextTrackIndex = if (newIndex > playList.lastIndex) 0 else newIndex
                currentTrack = playList[nextTrackIndex]
                currentTrack?.let { playerEventsPublisher.publish(PlayerServiceEvent.OnNextPressed(it)) }
            } else
                playerEventsPublisher.publish(PlayerServiceEvent.OnStopPressed)
        }
    }

    override fun play() {
        try {
            playListHandler.currentTrack?.file?.path?.let {
                if (isPaused) {
                    mediaPlayer.start()
                    isPaused = false
                } else
                    startPlay(it)
            }
        } catch (e: Exception) {
            playNext()
        }

    }

    private fun startPlay(filePath: String) {
        try {
            mediaPlayer.run {
                reset()
                setDataSource(filePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            playNext()
        }
    }

    override fun pause() {
        try {
            mediaPlayer.pause()
            isPaused = true
        } catch (e: Exception) {
            safeLog("MEDIA_PLAYER", e.message!!)
        }

    }

    override fun next() {
        isPaused = false
        playListHandler.currentTrack?.file?.path?.let { startPlay(it) }
    }

    override fun release() {
        mediaPlayer.run {
            try {
                stop()
            } catch (e: Exception) {
                safeLog("MEDIA_PLAYER", e.message!!)
            } finally {
                release()
            }
        }
    }
}