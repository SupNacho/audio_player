package ru.supnacho.audioplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.core.app.NotificationCompat
import io.reactivex.disposables.CompositeDisposable
import ru.supnacho.audioplayer.R
import ru.supnacho.audioplayer.data.storage.LocalStorageBoundary
import ru.supnacho.audioplayer.di.DaggerPlayerDependenciesComponent
import ru.supnacho.audioplayer.domain.events.*
import ru.supnacho.audioplayer.domain.player.MediaPlayerController
import ru.supnacho.audioplayer.domain.player.PlayListHandler
import ru.supnacho.audioplayer.screen.MainActivity
import ru.supnacho.audioplayer.utils.safeLog
import ru.supnacho.audioplayer.utils.subscribeAndTrack
import javax.inject.Inject

class PlayerService : Service() {
    private companion object {
        const val CHANNEL_ID = "NachoPlayerService"
        const val SESSION_TAG = "NachoPlayer"
        const val STOP_ACTION = "ru.supnacho.audioplayer.stopService"
        const val PLAY_ACTION = "ru.supnacho.audioplayer.playService"
        const val PAUSE_ACTION = "ru.supnacho.audioplayer.pauseService"
        const val NEXT_ACTION = "ru.supnacho.audioplayer.nextService"
    }

    private val disposables = CompositeDisposable()
    private var manager: NotificationManager? = null
    private var isPlaying = false

    @Inject
    lateinit var playListHandler: PlayListHandler

    @Inject
    lateinit var playerEventsProvider: PlayerEventsProvider

    @Inject
    lateinit var playerEventsPublisher: PlayerEventsPublisher

    @Inject
    lateinit var mediaPlayerController: MediaPlayerController

    @Inject
    lateinit var localStorage: LocalStorageBoundary

    override fun onCreate() {
        super.onCreate()
        DaggerPlayerDependenciesComponent.factory().create(this).inject(this)
        playerEventsProvider.provide().subscribeAndTrack(
            subscriptionsHolder = disposables,
            onSuccess = { handleEventFromUi(it) },
            onError = { Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show() }
        )

    }

    private fun handleEventFromUi(event: PlayerEvents) {
        safeLog("SERVICE EVENT", event.toString())
        when (event) {
            is PlayerServiceEvent -> {
                when (event) {
                    is PlayerServiceEvent.OnNextPressed -> updateNotificationAndService()
                    else -> { }
                }
            }
            is PlayerUiEvent -> {
                when (event) {
                    PlayerUiEvent.OnPlayPressed -> {
                        isPlaying = true
                        mediaPlayerController.play()
                    }
                    PlayerUiEvent.OnPausePressed -> {
                        isPlaying = false
                        mediaPlayerController.pause()
                    }
                    PlayerUiEvent.OnNextPressed -> playNextTrack()
                    PlayerUiEvent.OnStopPressed -> onStopAction()
                    is PlayerUiEvent.OnPlaySelected -> playSelectedTrack(event)
                }
            }
        }
    }

    private fun playSelectedTrack(selected: PlayerUiEvent.OnPlaySelected) {
        isPlaying = true
        playListHandler.run {
            currentTrack = selected.file
            playerEventsPublisher.publish(PlayerServiceEvent.OnNextPressed(selected.file))
            mediaPlayerController.next()
        }
    }

    private fun playNextTrack() {
        playListHandler.run {
            if (playList.isNotEmpty()) {
                isPlaying = true
                val newIndex = playList.indexOf(currentTrack) + 1
                val nextTrackIndex = if (newIndex > playList.lastIndex) 0 else newIndex
                currentTrack = playList[nextTrackIndex]
                currentTrack?.let { fm ->
                    playerEventsPublisher.publish(PlayerServiceEvent.OnNextPressed(fm))
                }
                mediaPlayerController.next()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            STOP_ACTION -> {
                onStopAction()
            }
            PLAY_ACTION -> {
                playerEventsPublisher.publish(PlayerServiceEvent.OnPlayPressed)
            }
            PAUSE_ACTION -> {
                playerEventsPublisher.publish(PlayerServiceEvent.OnPausePressed)
            }
            NEXT_ACTION -> {
                playNextTrack()
            }
            else -> {
            }
        }

        updateNotificationAndService()


        return START_NOT_STICKY
    }

    private fun updateNotificationAndService() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val stopSelf = Intent(this, PlayerService::class.java).apply { action = STOP_ACTION }
        val stopIntent =
            PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_UPDATE_CURRENT)
        val playSelf = Intent(this, PlayerService::class.java).apply { action = PLAY_ACTION }
        val playIntent =
            PendingIntent.getService(this, 0, playSelf, PendingIntent.FLAG_UPDATE_CURRENT)
        val pauseSelf = Intent(this, PlayerService::class.java).apply { action = PAUSE_ACTION }
        val pauseIntent =
            PendingIntent.getService(this, 0, pauseSelf, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextSelf = Intent(this, PlayerService::class.java).apply { action = NEXT_ACTION }
        val nextIntent =
            PendingIntent.getService(this, 0, nextSelf, PendingIntent.FLAG_UPDATE_CURRENT)

        val mediaSessionCompat = MediaSessionCompat(this, SESSION_TAG)
        val playIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val prepPlayIntent = if (isPlaying) pauseIntent else playIntent
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_player)
            .setContentTitle(
                getString(
                    R.string.notification_title,
                    playListHandler.currentTrack?.file?.name ?: ""
                )
            )
            .setContentIntent(pendingIntent)
            .addAction(playIcon, "PLAY", prepPlayIntent)
            .addAction(R.drawable.ic_stop, "STOP", stopIntent)
            .addAction(R.drawable.ic_next, "NEXT", nextIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .build()
        startForeground(1, notification)
    }

    private fun onStopAction() {
        manager?.cancel(0)
        mediaPlayerController.release()
        playerEventsPublisher.publish(PlayerServiceEvent.OnStopPressed)
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Nacho Player Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.setSound(null, null)
            manager = getSystemService(
                NotificationManager::class.java
            )
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        playListHandler.currentTrack?.file?.path?.let { localStorage.saveLastState(it) }
        disposables.clear()
        super.onDestroy()
    }
}