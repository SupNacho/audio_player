package ru.supnacho.audioplayer.domain.player

import ru.supnacho.audioplayer.domain.model.FileModel

interface PlayListHandler {
    var playList: List<FileModel>
    var currentTrack: FileModel?
}

class PlayListHandlerImpl:
    PlayListHandler {
    override var playList: List<FileModel> = emptyList()
    override var currentTrack: FileModel? = null
}