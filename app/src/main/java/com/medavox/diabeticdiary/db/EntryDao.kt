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


    @Query("SELECT * FROM Entry WHERE time > :since AND entryType = \"QA\" AND data != \"\"")
    fun getRecentQA(since:Long):List<Entry>

    @Query("SELECT * FROM Entry WHERE time > :since AND entryType = \"CP\" AND data != \"\"")
    fun getRecentCP(since:Long):List<Entry>

    @Query("SELECT * FROM Entry WHERE time > :since AND entryType = \"BI\" AND data != \"\"")
    fun getRecentBI(since:Long):List<Entry>

    //WHERE entryType = '${EntryTypes.BloodGlucose.naam}
    @Query("SELECT * FROM Entry WHERE entryType = \"BG\" ORDER BY time LIMIT :numberToGet")
    fun getLastBG(numberToGet:Int):List<Entry>

    /**@return null if the index is out of range.*/
    @Query("SELECT * FROM Entry ORDER BY time DESC LIMIT 1 OFFSET :index")
    fun getNthMostRecentEntry(index:Int):Entry?

    //do we need the argument startTime??
    /*@Query("SELECT * FROM Entry WHERE time < :startTime ORDER BY time DESC LIMIT 1 OFFSET :index")
    fun getNthMostRecentEntryFromTime(index:Int, startTime:Long):Entry*/

    @Query("SELECT COUNT(*) FROM Entry")
    fun getNumberOfEntries():Int
}
