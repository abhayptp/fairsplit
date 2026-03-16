package com.fairsplit.app.ui.groupdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fairsplit.app.data.db.entity.Expense
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.databinding.ItemExpenseBinding

class ExpensesAdapter(
    private val onDelete: (Expense) -> Unit
) : ListAdapter<Expense, ExpensesAdapter.ViewHolder>(DIFF) {

    private var usersMap: Map<Long, User> = emptyMap()

    fun setUsers(users: List<User>) {
        usersMap = users.associateBy { it.id }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.expenseDescription.text = expense.description
            val paidByName = usersMap[expense.paidByUserId]?.name ?: "Unknown"
            binding.expensePaidBy.text = "Paid by $paidByName"
            binding.expenseAmount.text = "$${"%.2f".format(expense.amount)}"
            binding.deleteButton.setOnClickListener { onDelete(expense) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(a: Expense, b: Expense) = a.id == b.id
            override fun areContentsTheSame(a: Expense, b: Expense) = a == b
        }
    }
}
