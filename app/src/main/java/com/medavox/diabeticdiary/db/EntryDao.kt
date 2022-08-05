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

    /**The String argument is necessary for Room to bind the method argument to the SQL query;
     * but it is not good practice on the JVM:
     * only one of "BG", "CP", "QA", "BI", "KT" OR "NOTES" should be accepted. */
    @Query("SELECT * FROM Entry WHERE time > :since AND entryType = :typeShortName AND data != \"\"")
    fun _dontUseThisDirectly(typeShortName:String, since:Long):List<Entry>

    /**Use this method instead, which accepts an enum containing the only valid types.
     * @param type the type of entries to return
     * @param since return entries which occurred after this time */
    fun getRecentEntriesOfType(type:EntryType, since:Long):List<Entry> {
        return _dontUseThisDirectly(type.shortName, since)
    }

    //WHERE entryType = '${EntryTypes.BloodGlucose.naam}
    @Query("SELECT * FROM Entry WHERE entryType = \"BG\" ORDER BY time DESC LIMIT :numberToGet")
    fun getLastBG(numberToGet:Int):List<Entry>

    /**@return null if the index is out of range.*/
    @Query("SELECT * FROM Entry ORDER BY time DESC LIMIT 1 OFFSET :index")
    fun getNthMostRecentEntry(index:Int):Entry?

    //grouped entries all have the same time.
    // a grouped entry can only have at most 1 entry of each type.
    // Further entries with the same type and time are listed in another grouped entry,
    // with the entry with the lower ID coming first.

    //fun getNthMostRecentGroupedEntry(index:Int):Map<EntryType, Entry>?

    /*going backwards through time, for each entry found,
        check if there are entries after this one with the "same" time: the same clock-minute

         we need to be able to index each of these time-groups
         use SQL GROUP-BY?
    * */

    //do we need the argument startTime??
    /*@Query("SELECT * FROM Entry WHERE time < :startTime ORDER BY time DESC LIMIT 1 OFFSET :index")
    fun getNthMostRecentEntryFromTime(index:Int, startTime:Long):Entry*/

    @Query("SELECT COUNT(*) FROM Entry")
    fun getNumberOfEntries():Int
}
