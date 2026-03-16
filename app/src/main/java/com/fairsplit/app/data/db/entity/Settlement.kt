package com.fairsplit.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settlements")
data class Settlement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
