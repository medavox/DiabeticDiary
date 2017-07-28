package com.medavox.diabeticdiary.db.entry;

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
}
