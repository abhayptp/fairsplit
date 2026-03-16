package com.fairsplit.app.data.repository

import com.fairsplit.app.data.db.dao.UserDao
import com.fairsplit.app.data.db.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun addUser(name: String): Long = userDao.insert(User(name = name))

    suspend fun deleteUser(user: User) = userDao.delete(user)
}
