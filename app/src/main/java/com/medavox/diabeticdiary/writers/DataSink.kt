package com.medavox.diabeticdiary.writers;

import android.content.Context;
import com.medavox.diabeticdiary.newdb.EntryType

/**
 * @author Adam Howard
 * @since 28/07/2017
 *
 * Implementers provide a method for logging/saving/outputting data
 */
interface DataSink {

    /**Perform a write of the provided data, in whatever way is pertinent to the implementor.
     * @param c
     * @param time the time the provided data is listed as having occurred.
     *             Not necessarily right now, but not in the future.
     * @param dataValues One string per potential entry, always.
*                   The ordering is the same as throughout the rest of the app.
* @return true if the write operation succeeded; false if it didn't, and should be re-attempted later
     * (meaning the entry cache should not be cleared  of this entry)*/
    fun write(c:Context, time:Long, dataValues:Map<EntryType, String>):Boolean
}
