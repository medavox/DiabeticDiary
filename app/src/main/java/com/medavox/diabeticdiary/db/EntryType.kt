package com.medavox.diabeticdiary.db

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
