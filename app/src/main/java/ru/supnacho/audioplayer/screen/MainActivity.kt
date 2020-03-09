package ru.supnacho.audioplayer.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.supnacho.audioplayer.R
import ru.supnacho.audioplayer.databinding.ActivityMainBinding
import ru.supnacho.audioplayer.domain.files.PathExtractor
import ru.supnacho.audioplayer.domain.model.FileModel
import ru.supnacho.audioplayer.screen.adapter.FilesRvAdapter
import ru.supnacho.audioplayer.screen.events.ScreenEvents
import ru.supnacho.audioplayer.screen.util.ViewModelFactory
import ru.supnacho.audioplayer.service.PlayerService
import ru.supnacho.audioplayer.utils.showOneButtonDialog
import ru.supnacho.audioplayer.utils.showTwoButtonDialog

class MainActivity : AppCompatActivity(), FilesRvAdapter.OnPlaySelectedFileListener {

    private lateinit var filesAdapter: FilesRvAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PlayerViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory()).get(PlayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            initRecycler()
            initButtons()
        }
        initViewStateSubscription()
        startPlayerService()
    }

    private fun initViewStateSubscription() {
        viewModel.viewState.observe(this, Observer { renderUi(it) })
        viewModel.viewStateEvents.observe(this, Observer { showError(it) })
    }

    private fun showError(error: ScreenEvents) {
        binding.srlRefreshFilesList.isRefreshing = false
        val errorRes = when (error) {
            ScreenEvents.noFiles -> R.string.files_reading_errors
            ScreenEvents.noDir -> R.string.dir_opening_errors
            ScreenEvents.ReplayingError -> R.string.replay_errors
        }
        showOneButtonDialog(
            message = getString(errorRes),
            buttonText = getString(R.string.common_ok)
        )
    }

    private fun startPlayerService() {
        startService(
            Intent(this@MainActivity, PlayerService::class.java)
        )
    }

    private fun renderUi(vs: ScreenViewState) {
        filesAdapter.data = vs.files
        binding.run {
            srlRefreshFilesList.isRefreshing = false
            tvSelectedFolder.text = vs.directoryPath.path
            when (vs.controlState) {
                ScreenViewState.ControlState.PLAYING -> ivPlayButton.setImageResource(R.drawable.ic_pause)
                ScreenViewState.ControlState.PAUSED -> ivPlayButton.setImageResource(R.drawable.ic_play)
                ScreenViewState.ControlState.STOPPED -> ivPlayButton.setImageResource(R.drawable.ic_play)
            }
        }
    }

    private fun ActivityMainBinding.initRecycler() {
        filesAdapter = FilesRvAdapter(this@MainActivity)
        rvAudioList.adapter = filesAdapter
    }

    private fun ActivityMainBinding.initButtons() {
        btnSelectFolder.setOnClickListener { openFolderChooser() }
        srlRefreshFilesList.setOnRefreshListener { viewModel.onRefresh() }
        ivPlayButton.setOnClickListener {
            val controlState = viewModel.viewState.value?.controlState
            if (controlState == ScreenViewState.ControlState.STOPPED || controlState == ScreenViewState.ControlState.PAUSED) {
                startPlayerService()
                viewModel.onPlayPressed()
            } else {
                viewModel.onPausePressed()
            }
        }
        ivStopButton.setOnClickListener { viewModel.onStopPressed() }
        ivNextButton.setOnClickListener {
            startPlayerService()
            viewModel.onNextPressed()
        }
    }

    override fun onSelectedFile(file: FileModel) {
        startPlayerService()
        viewModel.onPlaySelected(file)
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
        if (isEnabledPermissionReadExternalStorage()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = TYPE_WAV
            }
            val chooserIntent = Intent.createChooser(intent, getString(R.string.folder_chooser))
            startActivityForResult(chooserIntent, FOLDER_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFolderChooser()
            } else {
                showPermissionAlert(this)
            }
        }
    }


    private fun showPermissionAlert(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }

        showTwoButtonDialog(
            title = getString(R.string.permission_header),
            message = getString(R.string.permission_body),
            positiveButtonText = getString(R.string.permission_btn_positive),
            negativeButtonText = getString(R.string.permission_btn_negative),
            onPositiveClickListener = { context.startActivity(intent) }
        )
    }

    private fun isEnabledPermissionReadExternalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val permissionsNotGranted = ArrayList<String>()
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissionsNotGranted.add(PERMISSIONS_READ_EXTERNAL_STORAGE[0])

            if (permissionsNotGranted.size > 0) {
                val request: Array<String?> = permissionsNotGranted.toTypedArray()
                requestPermissions(request, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                return false
            }
        }
        return true
    }


    private companion object {
        const val FOLDER_REQUEST_CODE = 101
        const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 103
        const val TYPE_WAV = "audio/x-wav"
        val PERMISSIONS_READ_EXTERNAL_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
