package com.medavox.diabeticdiary.db.entry;

/**
 * @author Adam Howard
 * @date 13/11/2017
 */

public abstract class EntryHelper<T extends Entry> {
    public abstract T from(String data, long time) throws NumberFormatException;

    public abstract String getSqlDataType();
    public abstract T[] toArray();
}
