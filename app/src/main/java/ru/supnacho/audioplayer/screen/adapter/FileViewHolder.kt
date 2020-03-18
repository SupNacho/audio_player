package ru.supnacho.audioplayer.screen.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.supnacho.audioplayer.R
import ru.supnacho.audioplayer.databinding.ItemFileBinding
import ru.supnacho.audioplayer.domain.model.FileModel

class FileViewHolder(private val binding: ItemFileBinding, private val listener: OnFileSelected): RecyclerView.ViewHolder(binding.root) {
    fun bind(file: FileModel){
        val playIcon = if (file.isCurrent) binding.root.context.getDrawable(R.drawable.ic_play) else null
        binding.run {
            tvFileName.text = file.file.name
            ivCurrentPlaying.setImageDrawable(playIcon)
            llItemRoot.setOnClickListener { listener.onSelectFile(file) }
        }
    }

    interface OnFileSelected {
        fun onSelectFile(file: FileModel)
    }
}