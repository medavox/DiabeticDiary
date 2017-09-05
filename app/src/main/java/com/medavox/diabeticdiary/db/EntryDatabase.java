package com.medavox.diabeticdiary.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.medavox.diabeticdiary.db.entry.BackgroundInsulinEntry;
import com.medavox.diabeticdiary.db.entry.BloodGlucoseEntry;
import com.medavox.diabeticdiary.db.entry.CarbPortionEntry;
import com.medavox.diabeticdiary.db.entry.QuickActingEntry;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class EntryDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    //This is one of the reasons I hate sql databases. So many constants
    public static final String COLUMN_TIME = "event_time";
    
    public static final String COLUMN_BG = "blood_glucose";
    public static final String COLUMN_CP = "carb_portion";
    public static final String COLUMN_QA = "quick_acting";
    public static final String COLUMN_BI = "background_insulin";
    public static final String COLUMN_KT = "ketones";
    public static final String COLUMN_NOTES = "notes";
    
    public static final String TABLE_BG = "bg_table";
    public static final String TABLE_CP = "cp_table";
    public static final String TABLE_QA = "qa_table";
    public static final String TABLE_BI = "bi_table";
    public static final String TABLE_KT = "kt_table";
    public static final String TABLE_NOTES = "notes_table";

    private static final String CONSTRAINT_BG = " REAL UNSIGNED";
    private static final String CONSTRAINT_CP = " REAL UNSIGNED";
    private static final String CONSTRAINT_QA = " SMALLINT UNSIGNED";
    private static final String CONSTRAINT_BI = " SMALLINT UNSIGNED";
    private static final String CONSTRAINT_KT = " REAL UNSIGNED";
    //varchar lengths aren't enforced in sqlite, so there's no point defining it
    private static final String CONSTRAINT_NOTES = " TEXT";

    private static SQLiteDatabase readableDB;
    private static SQLiteDatabase writableDB;
    private static SQLiteOpenHelper instance;

    private static final String TAG = "EntryDatabase";
    
    public final static String[] tableNames = new String[]{TABLE_BG, TABLE_CP, TABLE_QA, TABLE_BI,
            TABLE_KT, TABLE_NOTES};
    public static final String[] columnNames = new String[]{COLUMN_BG, COLUMN_CP, COLUMN_QA,
            COLUMN_BI, COLUMN_KT, COLUMN_NOTES};
    private static final String[] constraints = new String[]{CONSTRAINT_BG, CONSTRAINT_CP,
            CONSTRAINT_QA, CONSTRAINT_BI, CONSTRAINT_KT, CONSTRAINT_NOTES};


    public EntryDatabase(Context c) {
        super(c, "DiabeticDiaryEntries", null, DATABASE_VERSION);
        instance = this;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(int i = 0; i < tableNames.length; i++) {
            String query = "CREATE TABLE IF NOT EXISTS " + tableNames[i]
                    + "(" + COLUMN_TIME + " INTEGER PRIMARY KEY NOT NULL UNIQUE, "
                    //no constraints anymore:
                    //better validation is done earlier in the chain,
                    //and having a uniform interface to adding different fields allows us
// to use array-access when adding rows: the same for-loop can add data to any table
                    + columnNames[i] /*+ constraints[i]*/ + " not null"
                    +  ");";
            sqLiteDatabase.compileStatement(query).execute();
        }
        Log.d("SQL", "tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //todo
        //when implementing, don't just delete the old tables (and all their data!), like with medi.
    }

    @Nullable
    public static SQLiteDatabase getReadableDB() {
        if(readableDB == null) {
            if(instance != null) {
                readableDB = instance.getReadableDatabase();
            }
            //maybe do something else if instance is also null?
        }
        return readableDB;
    }

    @Nullable
    public static SQLiteDatabase getWritableDB() {
        if(writableDB == null) {
            if(instance != null) {
                writableDB = instance.getWritableDatabase();
            }
            //maybe do something else if instance is also null?
        }
        return writableDB;
    }

    @NonNull
    public static QuickActingEntry[] getRecentQA(SQLiteDatabase db) {
        long fourHoursFiveMinutesAgo = System.currentTimeMillis() - ((4 * 3600 * 1000) + (5 * 60000));
        Cursor recentQA = db.query(TABLE_QA, null,
                COLUMN_TIME+" > "+fourHoursFiveMinutesAgo, null, null, null, COLUMN_TIME+" ASC");
        if(recentQA == null || recentQA.getCount() == 0) {
            return new QuickActingEntry[]{};
        }
        QuickActingEntry[] flob = new QuickActingEntry[recentQA.getCount()];
        int i = 0;
        int timeColumn = recentQA.getColumnIndex(COLUMN_TIME);
        int qaColumn = recentQA.getColumnIndex(COLUMN_QA);
        for(recentQA.moveToFirst(); !recentQA.isAfterLast(); recentQA.moveToNext()) {
            flob[i] = new QuickActingEntry(recentQA.getString(qaColumn), recentQA.getLong(timeColumn));
            i++;
        }
        recentQA.close();
        return flob;
    }

    @NonNull
    public static CarbPortionEntry[] getRecentCP(SQLiteDatabase db) {
        long fourHoursFiveMinutesAgo = System.currentTimeMillis() - ((4 * 3600 * 1000) + (5 * 60000));
        Cursor recentCP = db.query(TABLE_CP, null,
                COLUMN_TIME+" > "+fourHoursFiveMinutesAgo, null, null, null, COLUMN_TIME+" ASC");
        if(recentCP == null || recentCP.getCount() == 0) {
            return new CarbPortionEntry[]{};
        }
        CarbPortionEntry[] flob = new CarbPortionEntry[recentCP.getCount()];
        int i = 0;
        int time = recentCP.getColumnIndex(COLUMN_TIME);
        int cp = recentCP.getColumnIndex(COLUMN_CP);
        for(recentCP.moveToFirst(); !recentCP.isAfterLast(); recentCP.moveToNext()) {
            flob[i] = new CarbPortionEntry(recentCP.getString(cp), recentCP.getLong(time));
            i++;
        }
        recentCP.close();
        return flob;
    }

    @Nullable
    public static BloodGlucoseEntry getLastBG(SQLiteDatabase db) {
        Cursor lastBG = db.query(TABLE_BG,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TIME+" DESC");
        Log.i(TAG, "last BG cursor length:"+(lastBG == null ? "<null>" : lastBG.getCount()));
        if(lastBG == null || lastBG.getCount() == 0) {
            return null;
        }
        int timeColumn = lastBG.getColumnIndex(COLUMN_TIME);
        int bgColumn = lastBG.getColumnIndex(COLUMN_BG);
        lastBG.moveToFirst();
        BloodGlucoseEntry bge = null;
        while(!lastBG.isAfterLast()) {
            try {
                bge = new BloodGlucoseEntry(lastBG.getString(bgColumn), lastBG.getLong(timeColumn));
                break;
            }
            //keep trying to find the most recent BG entry that is valid, until we run out
            catch(NumberFormatException nfe) {
                Log.e(TAG, "Sqlite data is invalid -- new BloodGlucoseEntry(\""
                        +lastBG.getString(bgColumn)+"\", "+lastBG.getLong(timeColumn)+
                        ")  produces \""+nfe.getLocalizedMessage()+"\". Trying next row...");
                lastBG.moveToNext();
                continue;
            }
        }
        lastBG.close();
        return bge;
    }


    @Nullable
    public static BackgroundInsulinEntry getLastBI(SQLiteDatabase db) {
        Cursor lastBI = db.query(TABLE_BI,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TIME+" DESC");
        Log.i(TAG, "last BI cursor length:"+(lastBI == null ? "<null>" : lastBI.getCount()));
        if(lastBI == null || lastBI.getCount() == 0) {
            return null;
        }
        int timeColumn = lastBI.getColumnIndex(COLUMN_TIME);
        int biColumn = lastBI.getColumnIndex(COLUMN_BI);
        lastBI.moveToFirst();
        BackgroundInsulinEntry bie = null;
        while(!lastBI.isAfterLast()) {
            try {
                bie = new BackgroundInsulinEntry(lastBI.getString(biColumn), lastBI.getLong(timeColumn));
                break;
            }
            //keep trying to find the most recent BI entry that is valid, until we run out
            catch(NumberFormatException nfe) {
                Log.e(TAG, "Sqlite data is invalid -- new BackgroundInsulinEntry(\""
                        +lastBI.getString(biColumn)+"\", "+lastBI.getLong(timeColumn)+
                        ")  produces \""+nfe.getLocalizedMessage()+"\". Trying next row...");
                lastBI.moveToNext();
                continue;
            }
        }
        lastBI.close();
        return bie;
    }
}
