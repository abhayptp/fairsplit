package com.fairsplit.app.data.db

import androidx.room.TypeConverter
import com.fairsplit.app.data.db.entity.SplitType

class Converters {
    @TypeConverter
    fun fromSplitType(value: SplitType): String = value.name

    @TypeConverter
    fun toSplitType(value: String): SplitType = SplitType.valueOf(value)
}
