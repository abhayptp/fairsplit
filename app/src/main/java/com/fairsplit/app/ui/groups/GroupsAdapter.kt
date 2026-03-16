package com.fairsplit.app.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fairsplit.app.data.db.entity.Group
import com.fairsplit.app.databinding.ItemGroupBinding

data class GroupWithCount(val group: Group, val memberCount: Int)

class GroupsAdapter(
    private val onClick: (Group) -> Unit,
    private val onDelete: (Group) -> Unit
) : ListAdapter<GroupWithCount, GroupsAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupWithCount) {
            binding.groupName.text = item.group.name
            binding.memberCount.text = "${item.memberCount} member${if (item.memberCount != 1) "s" else ""}"
            binding.root.setOnClickListener { onClick(item.group) }
            binding.deleteButton.setOnClickListener { onDelete(item.group) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<GroupWithCount>() {
            override fun areItemsTheSame(a: GroupWithCount, b: GroupWithCount) = a.group.id == b.group.id
            override fun areContentsTheSame(a: GroupWithCount, b: GroupWithCount) = a == b
        }
    }
}
