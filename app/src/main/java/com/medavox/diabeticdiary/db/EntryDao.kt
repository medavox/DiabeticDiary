package com.medavox.diabeticdiary.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


/**
 * @author Adam Howard
 * @since 28/07/2017
 */
@Dao
interface EntryDao {

    @Insert
    fun insertEntries(entries:List<Entry>)

    @Insert
    fun insertEntries(vararg entries:Entry)

    //BG, CP, QA, BI, KT, NOTES}
    //private static final long FOUR_HOURS_FIVE_MINUTES_MS = (4 * 3600 * 1000) + (5 * 60000)
    //private static final long TWENTY_FIVE_HOURS_MS = 25 * 60 * 60 * 1000
    @Query("SELECT * FROM Entry WHERE time > :inTheLast")
    fun getRecentEntries(inTheLast:Long):List<Entry>


    @Query("SELECT * FROM Entry WHERE time > ${Entry.QA_DURATION_MS} AND entryType = \"EntryType.QuickActing\"")
    fun getRecentQA():List<Entry>

    @Query("SELECT * FROM Entry WHERE time > ${Entry.QA_DURATION_MS} AND entryType = \"EntryType.CarbPortion\"")
    fun getRecentCP():List<Entry>

    @Query("SELECT * FROM Entry WHERE time > ${Entry.BI_DURATION_MS} AND entryType = \"EntryType.BackgroundInsulin\"")
    fun getRecentBI():List<Entry>

    //WHERE entryType = '${EntryTypes.BloodGlucose.naam}
    @Query("SELECT * FROM Entry LIMIT :numberToGet")
    fun getLastBG(numberToGet:Int):List<Entry>
}
