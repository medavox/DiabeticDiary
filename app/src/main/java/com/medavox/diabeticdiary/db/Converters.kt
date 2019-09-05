package com.medavox.diabeticdiary.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun EntryType2String(entryType: EntryType?): String? {
        return entryType?.let { it.naam }
    }

    @TypeConverter
    fun String2EntryType(string: String?): EntryType? {
        return if (string == null) null else EntryType.values().first { it.naam == string }
    }
}