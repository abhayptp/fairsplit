package com.fairsplit.app.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "group_members",
    primaryKeys = ["groupId", "userId"]
)
data class GroupMember(
    val groupId: Long,
    val userId: Long
)
