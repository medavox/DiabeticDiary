package com.medavox.diabeticdiary;

import android.support.annotation.Nullable;

/**
 * @author Adam Howard
 * @since 01/09/2017
 */

public class CarbIngredient {

    private String name;//optional
    private int grams;
    private int percentCarb;

    public CarbIngredient(int grams, int percentCarb) {
        this.grams = grams;
        this.percentCarb = percentCarb;
    }

    public CarbIngredient(String name, int grams, int percentCarb) {
        this.name = name;
        this.grams = grams;
        this.percentCarb = percentCarb;
    }

    /**Calculate and return the total CP in this ingredient*/
    public int getCPx1000() {
        return percentCarb * grams;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public int getGrams() {
        return grams;
    }

    public int getPercentCarb() {
        return percentCarb;
    }

    @Override
    public String toString() {
        return "CarbIngredient ["+(name == null ?  "" : "\""+name+"\" ")+grams+"g, "+percentCarb+"%]";
    }
}
