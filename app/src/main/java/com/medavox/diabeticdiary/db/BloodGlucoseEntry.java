package com.medavox.diabeticdiary.db;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class BloodGlucoseEntry extends Entry{
    private float bg;
    public BloodGlucoseEntry(float bg, long time) {
        super(time);
        this.bg = bg;
    }

    public float getBloodGlucose() {
        return bg;
    }
}
