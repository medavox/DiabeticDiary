package com.medavox.diabeticdiary.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun EntryType2String(entryType: EntryType?): String {
        return entryType?.shortName ?: "<null>"
    }

    @TypeConverter
    fun String2EntryType(string: String?): EntryType {
        return if (string == null) {
            EntryType.Unknown
        } else {
            EntryType.values().first { it.shortName == string }
        }
    }
}