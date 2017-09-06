package com.medavox.diabeticdiary.db.entry;

/**
 * @author Adam Howard
 * @date 28/07/2017
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

    public float getCarbPortion() {
        return ((float) carbGrams) / 10;
    }
}
