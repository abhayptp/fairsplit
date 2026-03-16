package com.fairsplit.app.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class UsersViewModel(private val repository: UserRepository) : ViewModel() {
    val users = repository.getAllUsers().asLiveData()

    fun addUser(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.addUser(name.trim()) }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch { repository.deleteUser(user) }
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            UsersViewModel(repository) as T
    }
}
