package com.fairsplit.app.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.databinding.ItemUserBinding

class UsersAdapter(
    private val onDelete: (User) -> Unit
) : ListAdapter<User, UsersAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.name
            binding.deleteButton.setOnClickListener { onDelete(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }
}
