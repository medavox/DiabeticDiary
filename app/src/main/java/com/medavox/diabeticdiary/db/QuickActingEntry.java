package com.medavox.diabeticdiary.db;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class QuickActingEntry extends Entry {
    private int qa;
    public QuickActingEntry(int qa, long time) {
        super(time);
        this.qa = qa;
    }

    public int getQuickActing() {
        return qa;
    }
}
