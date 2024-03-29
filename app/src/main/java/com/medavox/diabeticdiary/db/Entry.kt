package com.medavox.diabeticdiary.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.medavox.util.io.DateTime
import java.util.regex.Pattern

/**
 * @author Adam Howard
 * @since 28/07/2017
 */
@Entity
data class Entry(val time:Long,
                 val entryType: EntryType,
                 val data:String,
                 @PrimaryKey(autoGenerate = true)
                 val id:Int=0) {

    init {
        //don't perform negative checks on this milliseconds-since-epoch datetime:
        //older diabetics (if this app gets released) might possibly want to record entries from
        //before 1 january 1970!
        val niceTime = DateTime.get(time, DateTime.TimeFormat.MINUTES)
        if (time < DIABETIC_EPOCH) {
            throw NumberFormatException("Entry time is too far in the past:" + niceTime)
        } else if (time > (System.currentTimeMillis() + 1000)) {
            throw NumberFormatException("Entry time cannot be in the future:" + niceTime)
        }
    }


    //Sqlite stores floats with a 0 decimal part (eg 5.0) without it (eg as "5").
    //todo:in order to account for this when creating Entry subclasses from rows,
    //allow for a missing decimal part
    companion object {
        @JvmStatic
        fun isValid(input:String, digitsBeforeZero:Int, digitsAfterZero:Int):Boolean {
            val decimalRegex =if(digitsAfterZero == 0)  "" else "(\\.[0-9]{0,"+digitsAfterZero+"})?"
            val pat = "[0-9]{0,"+digitsBeforeZero+"}"+decimalRegex
            val mPattern = Pattern . compile (pat)
            return mPattern.matcher(input).matches()
        }

        @Ignore
        //11 january 1922: the first time a human was injected with insulin
        //a fitting minimum date.
        private val DIABETIC_EPOCH = -1513857600000L

        val sixHoursAgo:Long get() {
            return System.currentTimeMillis() - (6L * 3600L * 1000L)
        }

        val twentyFourHoursAgo:Long get() {
            return System.currentTimeMillis() - (24L * 3600L * 1000L)
        }

        val twentySixHoursAgo:Long get() {
            return System.currentTimeMillis() - (26L * 60L * 60L * 1000L)
        }
    }

    override fun toString():String {
        return when(entryType) {
            EntryType.BloodGlucose -> "$data BG"
            EntryType.CarbPortion -> "$data CP"
            EntryType.QuickActing -> "$data QA"
            EntryType.BackgroundInsulin -> "$data BI"
            EntryType.Ketones -> "$data KT"
            else -> data
        }+" at "+DateTime.getTime(time, DateTime.TimeFormat.MINUTES)+" on "+
                DateTime.getDate(time, DateTime.DateFormat.BRIEF_WITH_DAY)
    }
}