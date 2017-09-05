package com.medavox.diabeticdiary.db.entry;

import com.medavox.util.io.DateTime;

import java.util.regex.Pattern;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

abstract class Entry {
    private long time;

    //11 january 1922: the first time a human was injected with insulin
    //a fitting minimum date.
    public static final long DIABETIC_EPOCH = -1513857600000L;

    public Entry(long time) {
        //don't perform negative checks on this milliseconds-since-epoch datetime:
        //older diabetics (if this app gets released) might possibly want to record entries from
        //before 1 january 1970!
        String niceTime = DateTime.get(time, DateTime.TimeFormat.MINUTES);
        if (time < DIABETIC_EPOCH) {
            throw new NumberFormatException("Entry time is too far in the past:" + niceTime);
        } else if (time > (System.currentTimeMillis() + 1000)) {
            throw new NumberFormatException("Entry time cannot be in the future:" + niceTime);
        }
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    //Sqlite stores floats with a 0 decimal part (eg 5.0) without it (eg as "5").
    //todo:in order to account for this when creating Entry subclasses from rows,
    //allow for a missing decimal part
    static boolean isValid(String input, int digitsBeforeZero, int digitsAfterZero) {
        String decimalRegex = (digitsAfterZero == 0 ? "" : "(\\.[0-9]{0," + digitsAfterZero + "})?");
        String pat = "[0-9]{0," + digitsBeforeZero + "}" + decimalRegex;
        Pattern mPattern = Pattern.compile(pat);
        return mPattern.matcher(input).matches();

    }
}