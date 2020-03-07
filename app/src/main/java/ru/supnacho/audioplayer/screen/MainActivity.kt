package ru.supnacho.audioplayer.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.supnacho.audioplayer.R
import ru.supnacho.audioplayer.databinding.ActivityMainBinding
import ru.supnacho.audioplayer.domain.files.PathExtractor
import ru.supnacho.audioplayer.screen.adapter.FilesRvAdapter
import ru.supnacho.audioplayer.screen.util.ViewModelFactory
import ru.supnacho.audioplayer.service.PlayerService

class MainActivity : AppCompatActivity() {

    private lateinit var filesAdapter: FilesRvAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PlayerViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory()).get(PlayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filesAdapter = FilesRvAdapter()
        binding.run {
            initRecycler()
            initButtons()
        }

        viewModel.viewState.observe(this, Observer { renderUi(it) })
    }

    private fun renderUi(vs: ScreenViewState){
        filesAdapter.data = vs.files
        binding.srlRefreshFilesList.isRefreshing = false
        binding.tvSelectedFolder.text = vs.directoryPath.path
        when(vs.controlState){
            ScreenViewState.ControlState.PLAYING -> binding.ivPlayButton.setImageResource(R.drawable.ic_pause)
            ScreenViewState.ControlState.PAUSED -> binding.ivPlayButton.setImageResource(R.drawable.ic_play)
            ScreenViewState.ControlState.STOPPED -> binding.ivPlayButton.setImageResource(R.drawable.ic_play)
        }
    }

    private fun ActivityMainBinding.initRecycler() {
        rvAudioList.adapter = filesAdapter
    }

    private fun ActivityMainBinding.initButtons() {
        btnSelectFolder.setOnClickListener { openFolderChooser() }
        srlRefreshFilesList.setOnRefreshListener { viewModel.onRefresh() }
        ivPlayButton.setOnClickListener {
            startService(
                Intent(
                    this@MainActivity,
                    PlayerService::class.java
                )
            )
            viewModel.onPlayPressed()
        }
        ivNextButton.setOnClickListener { viewModel.onNextPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FOLDER_REQUEST_CODE) {
            data?.dataString?.toUri()?.let {
                    viewModel.getFilesList(PathExtractor.getPath(this@MainActivity, it))
            }
        }
    }

    private fun openFolderChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
        }
        val chooserIntent = Intent.createChooser(intent, getString(R.string.folder_chooser))
        startActivityForResult(chooserIntent, FOLDER_REQUEST_CODE)
    }

    private companion object {
        const val FOLDER_REQUEST_CODE = 101
    }
}
