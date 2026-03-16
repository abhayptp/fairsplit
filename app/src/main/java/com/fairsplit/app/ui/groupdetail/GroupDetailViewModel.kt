package com.fairsplit.app.ui.groupdetail

import androidx.lifecycle.*
import com.fairsplit.app.data.db.entity.Expense
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.data.repository.BalanceEntry
import com.fairsplit.app.data.repository.ExpenseRepository
import com.fairsplit.app.data.repository.GroupRepository
import com.fairsplit.app.data.repository.SettlementRepository
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val groupId: Long,
    private val groupRepository: GroupRepository,
    private val expenseRepository: ExpenseRepository,
    private val settlementRepository: SettlementRepository
) : ViewModel() {

    val expenses = expenseRepository.getExpensesForGroup(groupId).asLiveData()
    val members = groupRepository.getMembersOfGroup(groupId).asLiveData()

    private val _balances = MutableLiveData<List<BalanceEntry>>()
    val balances: LiveData<List<BalanceEntry>> = _balances

    fun refreshBalances() {
        viewModelScope.launch {
            _balances.value = groupRepository.calculateGroupBalances(groupId)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
            refreshBalances()
        }
    }

    fun settle(fromUser: User, toUser: User, amount: Double) {
        viewModelScope.launch {
            settlementRepository.recordSettlement(groupId, fromUser.id, toUser.id, amount)
            refreshBalances()
        }
    }

    class Factory(
        private val groupId: Long,
        private val groupRepository: GroupRepository,
        private val expenseRepository: ExpenseRepository,
        private val settlementRepository: SettlementRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GroupDetailViewModel(groupId, groupRepository, expenseRepository, settlementRepository) as T
    }
}
