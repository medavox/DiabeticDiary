package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.TypeConverter

/**
 * @author Adam Howard
@date 14/04/2019
 */
enum class EntryType(@JvmField val naam:String, @JvmField val shortName:String) {
    BloodGlucose("Blood Glucose", "BG"),
    CarbPortion("Carbohydrate Portion", "CP"),
    QuickActing("Quick-Acting Insulin", "QA"),
    BackgroundInsulin("Background Insulin", "BI"),
    Ketones("Ketones", "KT"),
    Notes("Notes", "NOTES")

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