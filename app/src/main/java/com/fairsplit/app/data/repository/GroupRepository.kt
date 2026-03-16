package com.fairsplit.app.data.repository

import com.fairsplit.app.data.db.dao.ExpenseDao
import com.fairsplit.app.data.db.dao.GroupDao
import com.fairsplit.app.data.db.dao.SettlementDao
import com.fairsplit.app.data.db.entity.Group
import com.fairsplit.app.data.db.entity.GroupMember
import com.fairsplit.app.data.db.entity.User
import kotlinx.coroutines.flow.Flow

data class BalanceEntry(
    val fromUser: User,
    val toUser: User,
    val amount: Double
)

class GroupRepository(
    private val groupDao: GroupDao,
    private val expenseDao: ExpenseDao,
    private val settlementDao: SettlementDao
) {
    fun getAllGroups(): Flow<List<Group>> = groupDao.getAllGroups()

    fun getMembersOfGroup(groupId: Long): Flow<List<User>> = groupDao.getMembersOfGroup(groupId)

    suspend fun createGroup(name: String, memberIds: List<Long>): Long {
        val groupId = groupDao.insertGroup(Group(name = name))
        memberIds.forEach { userId ->
            groupDao.insertGroupMember(GroupMember(groupId = groupId, userId = userId))
        }
        return groupId
    }

    suspend fun deleteGroup(groupId: Long) {
        val group = groupDao.getGroupById(groupId) ?: return
        groupDao.deleteGroupMembers(groupId)
        groupDao.deleteGroup(group)
    }

    suspend fun calculateGroupBalances(groupId: Long): List<BalanceEntry> {
        val members = groupDao.getMembersOfGroupSync(groupId)
        val memberMap = members.associateBy { it.id }

        // net[userId] = positive means others owe this user, negative means this user owes others
        val net = mutableMapOf<Long, Double>()
        members.forEach { net[it.id] = 0.0 }

        val expenses = expenseDao.getExpensesForGroupSync(groupId)
        val expenseIds = expenses.map { it.id }
        val allSplits = if (expenseIds.isNotEmpty()) expenseDao.getSplitsForExpenses(expenseIds) else emptyList()
        val splitsByExpense = allSplits.groupBy { it.expenseId }

        for (expense in expenses) {
            val splits = splitsByExpense[expense.id] ?: continue
            // payer is owed the full amount
            net[expense.paidByUserId] = (net[expense.paidByUserId] ?: 0.0) + expense.amount
            // each member owes their split portion
            for (split in splits) {
                net[split.userId] = (net[split.userId] ?: 0.0) - split.amount
            }
        }

        // Account for settlements
        val settlements = settlementDao.getSettlementsForGroupSync(groupId)
        for (s in settlements) {
            net[s.fromUserId] = (net[s.fromUserId] ?: 0.0) - s.amount
            net[s.toUserId] = (net[s.toUserId] ?: 0.0) + s.amount
        }

        // Simplify debts: greedy algorithm
        return simplifyDebts(net, memberMap)
    }

    private fun simplifyDebts(
        net: Map<Long, Double>,
        memberMap: Map<Long, User>
    ): List<BalanceEntry> {
        val creditors = net.entries.filter { it.value > 0.01 }
            .map { it.key to it.value }.toMutableList()
        val debtors = net.entries.filter { it.value < -0.01 }
            .map { it.key to -it.value }.toMutableList()

        val result = mutableListOf<BalanceEntry>()
        var ci = 0
        var di = 0
        var creditBalance = if (creditors.isNotEmpty()) creditors[0].second else 0.0
        var debtBalance = if (debtors.isNotEmpty()) debtors[0].second else 0.0

        while (ci < creditors.size && di < debtors.size) {
            val creditorId = creditors[ci].first
            val debtorId = debtors[di].first
            val settled = minOf(creditBalance, debtBalance)
            val creditorUser = memberMap[creditorId]
            val debtorUser = memberMap[debtorId]
            if (creditorUser != null && debtorUser != null) {
                result.add(BalanceEntry(fromUser = debtorUser, toUser = creditorUser, amount = settled))
            }
            creditBalance -= settled
            debtBalance -= settled
            if (creditBalance < 0.01) {
                ci++
                if (ci < creditors.size) creditBalance = creditors[ci].second
            }
            if (debtBalance < 0.01) {
                di++
                if (di < debtors.size) debtBalance = debtors[di].second
            }
        }
        return result
    }
}
