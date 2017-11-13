package com.medavox.diabeticdiary.db.entry;

import com.medavox.diabeticdiary.db.EntryDatabase;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class KetonesEntry extends Entry {
    private float kt;
    public KetonesEntry(String kt, long time) throws NumberFormatException {
        super(time);
        if(!isValid(kt, 2, 1)) {
            throw new NumberFormatException("invalid Ketone entry passed to constructor:\""+kt+"\"");
        }
        this.kt=Float.parseFloat(kt);
    }

    public float getKetones() {
        return kt;
    }
    public static final EntryHelper<KetonesEntry> HELPER = new EntryHelper<KetonesEntry>() {

        @Override
        public KetonesEntry from(String kt, long time) throws NumberFormatException {
            return new KetonesEntry(kt, time);
        }

        @Override
        public String getSqlDataType() {
            return EntryDatabase.DATA_KT;
        }

        @Override
        public KetonesEntry[] toArray() {
            return new KetonesEntry[0];
        }
    };

    @Override
    public String toString() {
        return kt+" KT "+super.toString();
    }
}
