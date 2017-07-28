package com.medavox.diabeticdiary.db.entry;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class NotesEntry extends Entry {
    private String notes;
    public NotesEntry(String notes, long time) {
        super(time);
        this.notes=notes;
    }

    public String getNotes() {
        return notes;
    }
}
