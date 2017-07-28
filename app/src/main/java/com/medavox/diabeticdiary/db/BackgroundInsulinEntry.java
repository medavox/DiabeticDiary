package com.medavox.diabeticdiary.db;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class BackgroundInsulinEntry extends Entry {
    private int bi;
    public BackgroundInsulinEntry(int bi, long time) {
        super(time);
        this.bi=bi;
    }

    public int getBackgroundInsulin() {
        return bi;
    }
}
