package com.fairsplit.app.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fairsplit.app.data.repository.GroupRepository
import com.fairsplit.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val groups = groupRepository.getAllGroups().asLiveData()
    val users = userRepository.getAllUsers().asLiveData()

    fun createGroup(name: String, memberIds: List<Long>) {
        if (name.isBlank() || memberIds.isEmpty()) return
        viewModelScope.launch { groupRepository.createGroup(name.trim(), memberIds) }
    }

    fun deleteGroup(groupId: Long) {
        viewModelScope.launch { groupRepository.deleteGroup(groupId) }
    }

    class Factory(
        private val groupRepository: GroupRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GroupsViewModel(groupRepository, userRepository) as T
    }
}
