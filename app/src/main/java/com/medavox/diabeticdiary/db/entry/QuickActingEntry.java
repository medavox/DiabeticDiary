package com.medavox.diabeticdiary.db.entry;

import com.medavox.diabeticdiary.db.EntryDatabase;

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

    public static final EntryHelper<QuickActingEntry> HELPER = new EntryHelper<QuickActingEntry>() {
        @Override
        public QuickActingEntry from(String data, long time) throws NumberFormatException {
            return new QuickActingEntry(data, time);
        }

        @Override
        public String getSqlDataType() {
            return EntryDatabase.DATA_QA;
        }

        @Override
        public QuickActingEntry[] toArray() {
            return new QuickActingEntry[0];
        }
    };

    public int getQuickActing() {
        return qa;
    }

    @Override
    public String toString() {
        return qa+" QA "+super.toString();
    }
}
