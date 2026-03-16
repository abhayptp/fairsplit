package com.fairsplit.app.data.repository

import com.fairsplit.app.data.db.dao.SettlementDao
import com.fairsplit.app.data.db.entity.Settlement

class SettlementRepository(private val settlementDao: SettlementDao) {
    suspend fun recordSettlement(groupId: Long, fromUserId: Long, toUserId: Long, amount: Double) {
        settlementDao.insertSettlement(
            Settlement(
                groupId = groupId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                amount = amount
            )
        )
    }
}
