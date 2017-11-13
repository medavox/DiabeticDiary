package com.medavox.diabeticdiary.db.entry;

import com.medavox.diabeticdiary.db.EntryDatabase;

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

    public static final EntryHelper<NotesEntry> HELPER = new EntryHelper<NotesEntry>() {
        @Override
        public NotesEntry from(String data, long time) throws NumberFormatException {
            return new NotesEntry(data, time);
        }

        @Override
        public String getSqlDataType() {
            return EntryDatabase.DATA_NOTES;
        }

        @Override
        public NotesEntry[] toArray() {
            return new NotesEntry[0];
        }
    };

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "notes: \""+notes+"\" "+super.toString();
    }
}
