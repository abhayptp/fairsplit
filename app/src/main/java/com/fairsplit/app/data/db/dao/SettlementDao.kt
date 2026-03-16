package com.fairsplit.app.data.db.dao

import androidx.room.*
import com.fairsplit.app.data.db.entity.Settlement
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementDao {
    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getSettlementsForGroup(groupId: Long): Flow<List<Settlement>>

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY createdAt DESC")
    suspend fun getSettlementsForGroupSync(groupId: Long): List<Settlement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: Settlement): Long
}
