package ru.supnacho.audioplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ru.supnacho.audioplayer.screen.MainActivity
import ru.supnacho.audioplayer.R
import ru.supnacho.audioplayer.di.DaggerPlayerDependenciesComponent
import ru.supnacho.audioplayer.domain.events.PlayerEventsProvider
import ru.supnacho.audioplayer.domain.events.PlayerEventsPublisher
import javax.inject.Inject

class PlayerService: Service() {
    private companion object {
        const val CHANNEL_ID = "NachoPlayerService"
        const val STOP_ACTION = "stopService"
    }

    private var manager: NotificationManager? = null

    @Inject
    lateinit var playerEventsProvider: PlayerEventsProvider
    @Inject
    lateinit var playerEventsPublisher: PlayerEventsPublisher

    override fun onCreate() {
        super.onCreate()
        DaggerPlayerDependenciesComponent.create().inject(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (STOP_ACTION == intent?.action){
            manager?.cancel(0)
            stopSelf()
        }

        val input = intent!!.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val stopSelf = Intent(this, PlayerService::class.java).apply { action = STOP_ACTION }
        val stopIntent = PendingIntent.getService(this, 0, stopSelf,PendingIntent.FLAG_CANCEL_CURRENT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_player)
            .setContentTitle("Audio player")
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_play, "PLAY", pendingIntent)
            .addAction(R.drawable.ic_pause, "PAUSE", pendingIntent)
            .addAction(R.drawable.ic_stop,"STOP", stopIntent)
            .addAction(R.drawable.ic_next,"NEXT", stopIntent)
            .build()
        startForeground(1, notification)


        return START_NOT_STICKY
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
            manager = getSystemService(
                NotificationManager::class.java
            )
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}