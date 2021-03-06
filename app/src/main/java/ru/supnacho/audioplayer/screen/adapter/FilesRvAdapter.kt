package ru.supnacho.audioplayer.screen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.supnacho.audioplayer.databinding.ItemFileBinding
import ru.supnacho.audioplayer.domain.model.FileModel

class FilesRvAdapter(private val listener: OnPlaySelectedFileListener) : RecyclerView.Adapter<FileViewHolder>(), FileViewHolder.OnFileSelected {
    var data: List<FileModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFileBinding.inflate(inflater, parent, false)
        return FileViewHolder(binding, this)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onSelectFile(file: FileModel) {
        listener.onSelectedFile(file)
    }

    interface OnPlaySelectedFileListener {
        fun onSelectedFile(file: FileModel)
    }
}