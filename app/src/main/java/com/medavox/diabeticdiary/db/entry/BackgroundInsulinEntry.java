package com.medavox.diabeticdiary.db.entry;

import com.medavox.diabeticdiary.db.EntryDatabase;

/**
 * @author Adam Howard
 * @since 28/07/2017
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

    public static final EntryHelper<BackgroundInsulinEntry> HELPER = new EntryHelper<BackgroundInsulinEntry>() {
        @Override
        public BackgroundInsulinEntry from(String data, long time) throws NumberFormatException {
            return new BackgroundInsulinEntry(data, time);
        }

        @Override
        public String getSqlDataType() {
            return EntryDatabase.DATA_BI;
        }

        @Override
        public BackgroundInsulinEntry[] toArray() {
            return new BackgroundInsulinEntry[0];
        }
    };


    public int getBackgroundInsulin() {
        return bi;
    }

    @Override
    public String toString() {
        return bi+" BI "+super.toString();
    }
}
