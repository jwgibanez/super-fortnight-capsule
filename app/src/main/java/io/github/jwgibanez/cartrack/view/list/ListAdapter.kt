package io.github.jwgibanez.cartrack.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.github.jwgibanez.cartrack.data.model.User
import io.github.jwgibanez.cartrack.databinding.ListItemBinding

class ListAdapter(
    private val click: (User) -> Unit,
    diffCallback: DiffUtil.ItemCallback<User>
) : ListAdapter<User, ItemViewHolder?>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder.create(parent, click)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current: User = getItem(position)
        holder.bind(current)
    }

    internal class Diff : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
}