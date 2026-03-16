package com.fairsplit.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SplitType { EQUAL, CUSTOM }

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val description: String,
    val amount: Double,
    val paidByUserId: Long,
    val splitType: SplitType = SplitType.EQUAL,
    val createdAt: Long = System.currentTimeMillis()
)
