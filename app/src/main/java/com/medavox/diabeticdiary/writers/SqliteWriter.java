package com.medavox.diabeticdiary.writers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.medavox.diabeticdiary.MainActivity;
import com.medavox.diabeticdiary.db.EntryDatabase;

import java.util.Arrays;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class SqliteWriter implements DataSink {
    private static final String TAG = "SqliteWriter";
    @Override
    public boolean write(Context c, long time, String[] dataValues) {
        for(int i = 0; i < dataValues.length; i++) {
            if(dataValues[i] != null) {
                ContentValues vals = new ContentValues(2);
                vals.put(EntryDatabase.COLUMN_TIME, time);

                vals.put(EntryDatabase.columnNames[i], dataValues[i]);
                EntryDatabase.getWritableDB()
                        .insertOrThrow(EntryDatabase.tableNames[i], null, vals);
                Log.i(TAG, "wrote to sqlite db:"+ Arrays.toString(dataValues));
            }
        }
        return true;
    }
}
