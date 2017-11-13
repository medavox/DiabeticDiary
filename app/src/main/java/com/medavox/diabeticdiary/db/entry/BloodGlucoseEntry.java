package com.medavox.diabeticdiary.db.entry;

import com.medavox.diabeticdiary.db.EntryDatabase;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class BloodGlucoseEntry extends Entry{
    private float bg;
    public BloodGlucoseEntry(String bg, long time) throws NumberFormatException {
        super(time);
        if(!isValid(bg, 2, 1)) {
            throw new NumberFormatException
                    ("invalid Blood Glucose reading passed to constructor:\""+bg+"\"");
        }
        this.bg = Float.parseFloat(bg);
    }

    public static final EntryHelper<BloodGlucoseEntry> HELPER = new EntryHelper<BloodGlucoseEntry>() {

        @Override
        public BloodGlucoseEntry[] toArray() {
            return new BloodGlucoseEntry[0];
        }


        @Override
        public BloodGlucoseEntry from(String data, long time) throws NumberFormatException {
            return new BloodGlucoseEntry(data, time);
        }


        @Override
        public String getSqlDataType() {
            return EntryDatabase.DATA_BG;
        }
    };

    public float getBloodGlucose() {
        return bg;
    }

    @Override
    public String toString() {
        return bg+" BG "+super.toString();
    }
}
