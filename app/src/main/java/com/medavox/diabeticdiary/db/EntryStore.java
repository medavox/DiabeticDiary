package com.medavox.diabeticdiary.db;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public interface EntryStore {
    /**get all the QA injections made in the last 4 hours 5 minutes.*/
    QuickActingEntry[] getRecentQA();
    /**Get all the carbohydrate portions consumed in the last 4 hours*/
    CarbPortionEntry[] getRecentCP();
    /**Get the most recently recorded blood glucose reading*/
    BloodGlucoseEntry getLastBG();

}
