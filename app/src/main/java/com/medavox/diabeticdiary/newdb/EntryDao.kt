package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query


/**
 * @author Adam Howard
 * @since 28/07/2017
 */
@Dao
interface EntryDao {

    @Insert
    fun insertEntries(entries:List<Entri>)

    @Insert
    fun insertEntries(vararg entries:Entri)

    //BG, CP, QA, BI, KT, NOTES}
    //private static final long FOUR_HOURS_FIVE_MINUTES_MS = (4 * 3600 * 1000) + (5 * 60000)
    //private static final long TWENTY_FIVE_HOURS_MS = 25 * 60 * 60 * 1000
    @Query("SELECT * FROM Entri WHERE time > :inTheLast")
    fun getRecentEntries(inTheLast:Long):List<Entri>


    @Query("SELECT * FROM Entri WHERE time > ${Entri.QA_DURATION_MS} AND WHERE entryType = QuickActing")
    fun getRecentQA():List<Entri>

    @Query("SELECT * FROM Entri WHERE time > ${Entri.QA_DURATION_MS} AND entryType = CarbPortion")
    fun getRecentCP():List<Entri>

    @Query("SELECT * FROM Entri WHERE time > ${Entri.BI_DURATION_MS} AND entryType = BackgroundInsulin")
    fun getRecentBI():List<Entri>

    //WHERE entryType = '${EntryTypes.BloodGlucose.naam}
    @Query("SELECT * FROM Entri LIMIT :numberToGet")
    fun getLastBG(numberToGet:Int):List<Entri>
}