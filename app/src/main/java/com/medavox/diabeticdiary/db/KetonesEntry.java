package com.medavox.diabeticdiary.db;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class KetonesEntry extends Entry {
    private float kt;
    public KetonesEntry(float kt, long time) {
        super(time);
        this.kt=kt;
    }

    public float getKetones() {
        return kt;
    }
}
