package com.medavox.diabeticdiary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class EntryDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    //This is one of the reasons I hate sql databases. So many constants
    private static final String COLUMN_TIME = "event_time";
    
    private static final String COLUMN_BG = "blood_glucose";
    private static final String COLUMN_CP = "carb_portion";
    private static final String COLUMN_QA = "quick_acting";
    private static final String COLUMN_BI = "background_insulin";
    private static final String COLUMN_KT = "ketones";
    private static final String COLUMN_NOTES = "notes";
    
    private static final String TABLE_BG = "bg_table";
    private static final String TABLE_CP = "cp_table";
    private static final String TABLE_QA = "qa_table";
    private static final String TABLE_BI = "bi_table";
    private static final String TABLE_KT = "kt_table";
    private static final String TABLE_NOTES = "notes_table";

    private static final String CONSTRAINT_BG = " REAL UNSIGNED";
    private static final String CONSTRAINT_CP = " REAL UNSIGNED";
    private static final String CONSTRAINT_QA = " SMALLINT UNSIGNED";
    private static final String CONSTRAINT_BI = " SMALLINT UNSIGNED";
    private static final String CONSTRAINT_KT = " REAL UNSIGNED";
    //varchar lengths aren't enforced in sqlite, so there's no point defining it
    private static final String CONSTRAINT_NOTES = " TEXT";

    
    private final static String[] tableNames = new String[]{TABLE_BG, TABLE_CP, TABLE_QA, TABLE_BI,
            TABLE_KT, TABLE_NOTES};
    private static final String[] columnNames = new String[]{COLUMN_BG, COLUMN_CP, COLUMN_QA,
            COLUMN_BI, COLUMN_KT, COLUMN_NOTES};
    private static final String[] constraints = new String[]{CONSTRAINT_BG, CONSTRAINT_CP,
            CONSTRAINT_QA, CONSTRAINT_BI, CONSTRAINT_KT, CONSTRAINT_NOTES};


    public EntryDatabase(Context c) {
        super(c, "DiabeticDiaryEntries", null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(int i = 0; i < tableNames.length; i++) {
            String query = "CREATE TABLE IF NOT EXISTS " + tableNames[i]
                    + "(" + COLUMN_TIME + " INTEGER PRIMARY KEY NOT NULL UNIQUE, "
                    + columnNames[i] + constraints[i] + " not null"
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
}
