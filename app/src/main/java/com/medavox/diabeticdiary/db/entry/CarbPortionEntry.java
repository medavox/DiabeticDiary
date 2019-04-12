package com.medavox.diabeticdiary.db.entry;

import com.medavox.diabeticdiary.db.EntryDatabase;

/**
 * @author Adam Howard
 * @since 28/07/2017
 */

public class CarbPortionEntry extends Entry {
    private int carbGrams;
    public CarbPortionEntry(String cp, long time) throws NumberFormatException {
        super(time);
        if(!isValid(cp, 3, 1)) {
            throw new NumberFormatException("Invalid Carb Portion passed to constructor:\""+cp+"\"");
        }
        float cpFloat = Float.parseFloat(cp);
        this.carbGrams = (int)(cpFloat * 10);
    }

    public static final EntryHelper<CarbPortionEntry> HELPER = new EntryHelper<CarbPortionEntry>() {
        @Override
        public CarbPortionEntry[] toArray() {
            return new CarbPortionEntry[0];
        }

        @Override
        public CarbPortionEntry from(String data, long time) throws NumberFormatException {
            return new CarbPortionEntry(data, time);
        }

        @Override
        public String getSqlDataType() {
            return EntryDatabase.DATA_CP;
        }
    };
    public float getCarbPortion() {
        return ((float) carbGrams) / 10;
    }

    @Override
    public String toString() {
        return getCarbPortion()+" CP "+super.toString();
    }
}
