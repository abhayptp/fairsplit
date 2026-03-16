package com.fairsplit.app.data.db.dao

import androidx.room.*
import com.fairsplit.app.data.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    suspend fun getUsersByIds(ids: List<Long>): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Delete
    suspend fun delete(user: User)
}
