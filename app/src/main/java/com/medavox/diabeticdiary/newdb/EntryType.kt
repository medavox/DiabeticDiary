package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.TypeConverter

/**
 * @author Adam Howard
@date 14/04/2019
 */
enum class EntryType(val naam:String) {
    BloodGlucose("Blood Glucose"),
    CarbPortion("Carbohydrate Portion"),
    QuickActing("Quick-Acting Insulin"),
    BackgroundInsulin("Background Insulin"),
    Ketones("Ketones"),
    Notes("Notes")

}
class TypeConverters {
    @TypeConverter
    fun EntryType2String(entryType: EntryType?): String? {
        return entryType?.let { it.naam }
    }

    @TypeConverter
    fun String2EntryType(string: String?): EntryType? {
        return if (string == null) null else EntryType.values().first { it.naam == string }
    }
}