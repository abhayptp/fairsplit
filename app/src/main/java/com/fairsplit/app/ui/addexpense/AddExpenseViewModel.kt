package com.fairsplit.app.ui.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fairsplit.app.data.repository.ExpenseRepository
import com.fairsplit.app.data.repository.GroupRepository
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val groupId: Long,
    private val groupRepository: GroupRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    val members = groupRepository.getMembersOfGroup(groupId).asLiveData()

    fun addExpense(
        description: String,
        amount: Double,
        paidByUserId: Long,
        memberIds: List<Long>,
        customAmounts: Map<Long, Double>? = null
    ) {
        if (description.isBlank() || amount <= 0 || memberIds.isEmpty()) return
        viewModelScope.launch {
            expenseRepository.addExpense(groupId, description.trim(), amount, paidByUserId, memberIds, customAmounts)
        }
    }

    class Factory(
        private val groupId: Long,
        private val groupRepository: GroupRepository,
        private val expenseRepository: ExpenseRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AddExpenseViewModel(groupId, groupRepository, expenseRepository) as T
    }
}
