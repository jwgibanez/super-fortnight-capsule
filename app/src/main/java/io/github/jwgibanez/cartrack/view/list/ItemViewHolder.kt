package io.github.jwgibanez.cartrack.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.jwgibanez.cartrack.data.model.User
import io.github.jwgibanez.cartrack.databinding.ListItemBinding

class ItemViewHolder private constructor(
    private val binding: ListItemBinding,
    private val onItemClick: (User) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.name.text = user.name
        itemView.setOnClickListener { onItemClick(user) }
    }

    companion object {
        fun create(parent: ViewGroup, onItemClick: (User) -> Unit): ItemViewHolder {
            return ItemViewHolder(
                ListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onItemClick
            )
        }
    }
}