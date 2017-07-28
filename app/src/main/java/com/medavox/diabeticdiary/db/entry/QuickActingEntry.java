package com.medavox.diabeticdiary.db.entry;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class QuickActingEntry extends Entry {
    private int qa;
    public QuickActingEntry(String qa, long time) throws NumberFormatException {
        super(time);
        if(!isValid(qa, 3, 0)) {
            throw new NumberFormatException
                    ("invalid Quick-Acting entry passed to constructor:\""+qa+"\"");
        }
        this.qa = Integer.parseInt(qa);
    }

    public int getQuickActing() {
        return qa;
    }
}
