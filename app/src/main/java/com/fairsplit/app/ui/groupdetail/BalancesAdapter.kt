package com.fairsplit.app.ui.groupdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fairsplit.app.data.repository.BalanceEntry
import com.fairsplit.app.databinding.ItemBalanceBinding

class BalancesAdapter(
    private val onSettle: (BalanceEntry) -> Unit
) : RecyclerView.Adapter<BalancesAdapter.ViewHolder>() {

    private var items: List<BalanceEntry> = emptyList()

    fun submitList(list: List<BalanceEntry>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: BalanceEntry) {
            binding.balanceText.text =
                "${entry.fromUser.name} owes ${entry.toUser.name} $${"%.2f".format(entry.amount)}"
            binding.settleButton.visibility = View.VISIBLE
            binding.settleButton.setOnClickListener { onSettle(entry) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
