package com.fairsplit.app

import android.app.Application
import com.fairsplit.app.data.db.FairSplitDatabase
import com.fairsplit.app.data.repository.ExpenseRepository
import com.fairsplit.app.data.repository.GroupRepository
import com.fairsplit.app.data.repository.SettlementRepository
import com.fairsplit.app.data.repository.UserRepository

class FairSplitApp : Application() {
    val database by lazy { FairSplitDatabase.getInstance(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val groupRepository by lazy {
        GroupRepository(database.groupDao(), database.expenseDao(), database.settlementDao())
    }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val settlementRepository by lazy { SettlementRepository(database.settlementDao()) }
}
