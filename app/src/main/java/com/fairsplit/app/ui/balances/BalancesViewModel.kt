package com.fairsplit.app.ui.balances

import androidx.lifecycle.*
import com.fairsplit.app.data.repository.BalanceEntry
import com.fairsplit.app.data.repository.GroupRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BalancesViewModel(private val groupRepository: GroupRepository) : ViewModel() {

    private val _balances = MutableLiveData<List<BalanceEntry>>()
    val balances: LiveData<List<BalanceEntry>> = _balances

    fun refresh() {
        viewModelScope.launch {
            val groups = groupRepository.getAllGroups().first()
            val allBalances = mutableListOf<BalanceEntry>()
            for (group in groups) {
                allBalances.addAll(groupRepository.calculateGroupBalances(group.id))
            }
            // Aggregate: net balance per (fromUser, toUser) pair
            val netMap = mutableMapOf<Pair<Long, Long>, BalanceEntry>()
            for (entry in allBalances) {
                val key = entry.fromUser.id to entry.toUser.id
                val reverseKey = entry.toUser.id to entry.fromUser.id
                when {
                    netMap.containsKey(key) -> {
                        val existing = netMap[key]!!
                        netMap[key] = existing.copy(amount = existing.amount + entry.amount)
                    }
                    netMap.containsKey(reverseKey) -> {
                        val existing = netMap[reverseKey]!!
                        val newAmt = existing.amount - entry.amount
                        if (newAmt > 0.01) {
                            netMap[reverseKey] = existing.copy(amount = newAmt)
                        } else if (newAmt < -0.01) {
                            netMap.remove(reverseKey)
                            netMap[key] = entry.copy(amount = -newAmt)
                        } else {
                            netMap.remove(reverseKey)
                        }
                    }
                    else -> netMap[key] = entry
                }
            }
            _balances.value = netMap.values.toList()
        }
    }

    class Factory(private val groupRepository: GroupRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BalancesViewModel(groupRepository) as T
    }
}
