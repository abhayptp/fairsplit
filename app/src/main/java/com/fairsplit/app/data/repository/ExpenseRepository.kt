package com.fairsplit.app.data.repository

import com.fairsplit.app.data.db.dao.ExpenseDao
import com.fairsplit.app.data.db.entity.Expense
import com.fairsplit.app.data.db.entity.ExpenseSplit
import com.fairsplit.app.data.db.entity.SplitType
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getExpensesForGroup(groupId: Long): Flow<List<Expense>> =
        expenseDao.getExpensesForGroup(groupId)

    suspend fun addExpense(
        groupId: Long,
        description: String,
        amount: Double,
        paidByUserId: Long,
        memberIds: List<Long>,
        customAmounts: Map<Long, Double>? = null
    ) {
        val splitType = if (customAmounts != null) SplitType.CUSTOM else SplitType.EQUAL
        val expenseId = expenseDao.insertExpense(
            Expense(
                groupId = groupId,
                description = description,
                amount = amount,
                paidByUserId = paidByUserId,
                splitType = splitType
            )
        )

        val splits = if (customAmounts != null) {
            memberIds.map { userId ->
                ExpenseSplit(expenseId = expenseId, userId = userId, amount = customAmounts[userId] ?: 0.0)
            }
        } else {
            val perPerson = amount / memberIds.size
            memberIds.map { userId ->
                ExpenseSplit(expenseId = expenseId, userId = userId, amount = perPerson)
            }
        }
        expenseDao.insertSplits(splits)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteSplitsForExpense(expense.id)
        expenseDao.deleteExpense(expense)
    }
}
