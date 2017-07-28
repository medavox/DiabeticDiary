package com.medavox.diabeticdiary.db;

import java.util.regex.Pattern;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

abstract class Entry {
    private long time;
    public Entry(long time){
        this.time = time;
    }
    public long getTime() {
        return time;
    }

    boolean isValid(String input, int digitsBeforeZero, int digitsAfterZero) {
        String decimalRegex = (digitsAfterZero == 0 ? "" : "\\.[0-9]{0," + digitsAfterZero + "}");
        String pat="[0-9]{0,"+digitsBeforeZero+"}"+decimalRegex;
        Pattern mPattern = Pattern.compile(pat);
        return mPattern.matcher(input).matches();

    }
}
