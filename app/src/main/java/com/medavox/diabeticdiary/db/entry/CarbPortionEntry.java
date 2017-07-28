package com.medavox.diabeticdiary.db.entry;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class CarbPortionEntry extends Entry {
    private float cp;
    public CarbPortionEntry(String cp, long time) throws NumberFormatException {
        super(time);
        if(!isValid(cp, 3, 1)) {
            throw new NumberFormatException("Invalid Carb Portion passed to constructor:\""+cp+"\"");
        }
        this.cp = Float.parseFloat(cp);
    }

    public float getCarbPortion() {
        return cp;
    }
}
