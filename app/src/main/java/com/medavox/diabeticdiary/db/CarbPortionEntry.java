package com.medavox.diabeticdiary.db;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class CarbPortionEntry extends Entry {
    private float cp;
    public CarbPortionEntry(float cp, long time) {
        super(time);
        this.cp = cp;
    }

    public float getCarbPortion() {
        return cp;
    }
}
