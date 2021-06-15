package com.elevenine.filedownloadapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elevenine.filedownloadapp.databinding.ItemFileBinding
import com.elevenine.filedownloadapp.databinding.ItemTitleBinding

class FileAdapter(private val onFileClick: (FileModel) -> Unit) :
    RecyclerView.Adapter<FileAdapter.VH>() {

    private val items = mutableListOf<Item>()

    fun addFiles(files: List<FileModel>) {
        files.forEach {
            this.items.add(ItemFile(it))
        }

        notifyDataSetChanged()
    }

    fun addTitle(title: String) {
        items.add(ItemTitle(title))
        notifyItemInserted(items.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when(viewType) {
            Item.ITEM_FILE ->
                FileVH(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->
                TitleVH(ItemTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: FileAdapter.VH, position: Int) {
        holder.initView(items[position])
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getType()

    interface Item {

        fun getType(): Int

        companion object {
            const val ITEM_FILE = 1
            const val ITEM_TITLE = 2
        }
    }

    inner class ItemFile(val file: FileModel) : Item {
        override fun getType() = Item.ITEM_FILE
    }

    inner class ItemTitle(val title: String) : Item {
        override fun getType() = Item.ITEM_TITLE
    }

    abstract class VH(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun initView(item: Item)
    }

    inner class FileVH(private val binding: ItemFileBinding) : VH(binding) {
        override fun initView(item: Item) {
            if (item !is ItemFile) return

            val file = item.file

            binding.tvFileName.text = file.name

            binding.root.setOnClickListener { onFileClick(file) }
        }
    }

    inner class TitleVH(private val binding: ItemTitleBinding) : VH(binding) {
        override fun initView(item: Item) {
            if (item !is ItemTitle) return

            binding.tvTitle.text = item.title
        }
    }
}