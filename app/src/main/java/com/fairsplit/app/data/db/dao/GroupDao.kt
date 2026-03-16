package com.fairsplit.app.data.db.dao

import androidx.room.*
import com.fairsplit.app.data.db.entity.Group
import com.fairsplit.app.data.db.entity.GroupMember
import com.fairsplit.app.data.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<Group>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): Group?

    @Query("""
        SELECT users.* FROM users
        INNER JOIN group_members ON users.id = group_members.userId
        WHERE group_members.groupId = :groupId
    """)
    fun getMembersOfGroup(groupId: Long): Flow<List<User>>

    @Query("""
        SELECT users.* FROM users
        INNER JOIN group_members ON users.id = group_members.userId
        WHERE group_members.groupId = :groupId
    """)
    suspend fun getMembersOfGroupSync(groupId: Long): List<User>

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    suspend fun getGroupMembers(groupId: Long): List<GroupMember>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupMember(member: GroupMember)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("DELETE FROM group_members WHERE groupId = :groupId")
    suspend fun deleteGroupMembers(groupId: Long)
}
