package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.TypeConverter

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