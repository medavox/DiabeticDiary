package com.medavox.diabeticdiary.db.entry;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class BackgroundInsulinEntry extends Entry {
    private int bi;
    public BackgroundInsulinEntry(String bi, long time) throws NumberFormatException {
        super(time);
        if(!isValid(bi, 3, 0)) {
            throw new NumberFormatException
                    ("invalid Background Insulin entry passed to constructor:\""+bi+"\"");
        }
        this.bi=Integer.parseInt(bi);
    }

    public int getBackgroundInsulin() {
        return bi;
    }
}
