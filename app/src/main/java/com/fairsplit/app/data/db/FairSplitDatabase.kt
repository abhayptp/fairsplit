package com.fairsplit.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fairsplit.app.data.db.dao.ExpenseDao
import com.fairsplit.app.data.db.dao.GroupDao
import com.fairsplit.app.data.db.dao.SettlementDao
import com.fairsplit.app.data.db.dao.UserDao
import com.fairsplit.app.data.db.entity.*

@Database(
    entities = [User::class, Group::class, GroupMember::class, Expense::class, ExpenseSplit::class, Settlement::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FairSplitDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settlementDao(): SettlementDao

    companion object {
        @Volatile
        private var INSTANCE: FairSplitDatabase? = null

        fun getInstance(context: Context): FairSplitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FairSplitDatabase::class.java,
                    "fairsplit.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
