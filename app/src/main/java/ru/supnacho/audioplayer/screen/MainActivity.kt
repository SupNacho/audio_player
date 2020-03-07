package ru.supnacho.audioplayer.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.supnacho.audioplayer.databinding.ActivityMainBinding
import ru.supnacho.audioplayer.di.playerDependencies
import ru.supnacho.audioplayer.screen.util.ViewModelFactory
import ru.supnacho.audioplayer.service.PlayerService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : PlayerViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory()).get(PlayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            ivPlayButton.setOnClickListener { startService(Intent(this@MainActivity, PlayerService::class.java)) }
            ivNextButton.setOnClickListener { viewModel.onNext() }
        }

    }
}
