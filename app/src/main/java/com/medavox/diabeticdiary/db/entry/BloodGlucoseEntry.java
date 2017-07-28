package com.medavox.diabeticdiary.db.entry;

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

    public float getBloodGlucose() {
        return bg;
    }
}
