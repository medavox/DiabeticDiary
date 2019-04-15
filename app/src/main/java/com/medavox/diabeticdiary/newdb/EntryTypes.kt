package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.TypeConverter

/**
 * @author Adam Howard
@date 14/04/2019
 */
enum class EntryTypez(val naam:String) {
    BloodGlucose("Blood Glucose"),
    CarbPortion("Carbohydrate Portion"),
    QuickActing("Quick-Acting Insulin"),
    BackgroundInsulin("Background Insulin"),
    Ketones("Ketones"),
    Notes("Notes")


}

@TypeConverter
fun EntryType2String(entryType:EntryTypez?):String? {
    return entryType?.let { it.naam }
}

@TypeConverter
fun String2EntryType(string:String?):EntryTypez? {
    return if (string == null) null else EntryTypez.valueOf(string)
}

/**
 * @author Adam Howard
@date 14/04/2019
 */
sealed class EntryTypes(val naam:String)
object BloodGlucose: EntryTypes("Blood Glucose")
object CarbPortion: EntryTypes("Carbohydrate Portion")
object QuickActing: EntryTypes("Quick-Acting Insulin")
object BackgroundInsulin: EntryTypes("Background Insulin")
object Ketones: EntryTypes("Ketones")
object Notesz: EntryTypes("Notes")