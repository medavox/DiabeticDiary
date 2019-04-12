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
import com.medavox.diabeticdiary.db.entry.Entry;
import com.medavox.diabeticdiary.db.entry.EntryHelper;
import com.medavox.diabeticdiary.db.entry.KetonesEntry;
import com.medavox.diabeticdiary.db.entry.NotesEntry;
import com.medavox.diabeticdiary.db.entry.QuickActingEntry;
import com.medavox.util.io.Bytes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Adam Howard
 * @since 28/07/2017
 */

public class EntryDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "entries_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "event_time";
    public static final String COLUMN_DATA = "entry_data";
    public static final String COLUMN_DATA_TYPE = "data_type";

    public static final String DATA_BG = "blood_glucose";
    public static final String DATA_CP = "carb_portion";
    public static final String DATA_QA = "quick_acting";
    public static final String DATA_BI = "background_insulin";
    public static final String DATA_KT = "ketones";
    public static final String DATA_NOTES = "notes";

    public static final String[] dataTypes = new String[]{DATA_BG, DATA_CP, DATA_QA, DATA_BI,
            DATA_KT, DATA_NOTES};

    private static SQLiteDatabase readableDB;
    private static SQLiteDatabase writableDB;
    private static SQLiteOpenHelper instance;

    private static final long FOUR_HOURS_FIVE_MINUTES_MS = (4 * 3600 * 1000) + (5 * 60000);
    private static final long TWENTY_FIVE_HOURS_MS = 25 * 60 * 60 * 1000;

    private static final String TAG = "EntryDatabase";
    
    public EntryDatabase(Context c) {
        super(c, "DiabeticDiaryEntries", null, DATABASE_VERSION);
        instance = this;
    }
    //TODO: add ID column to single table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
                //no constraints anymore:
                //better validation is done earlier in the chain,
                //and having a uniform interface to adding different fields allows us
// to use array-access when adding rows: the same for-loop can add data to any table
        String query = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
                + "(" + COLUMN_TIME + " INTEGER NOT NULL, "
                + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL UNIQUE, "
                + COLUMN_DATA + " NOT NULL, "
                + COLUMN_DATA_TYPE + " NOT NULL"
                +  ");";
            sqLiteDatabase.compileStatement(query).execute();
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

    private static <T extends Entry> T[] getRecentEntries(SQLiteDatabase db,
                                                          long inTheLast,
                                                          EntryHelper<T> helper) {
        long timeAgo = System.currentTimeMillis() - inTheLast;
        String selec = COLUMN_TIME+" > "+timeAgo;
        return EntryDatabase.getRecentEntries(db, selec, helper);

    }

    private static <T extends Entry> T[] getRecentEntries(@NonNull SQLiteDatabase db,
                                                          @NonNull EntryHelper<T> helper) {
        return EntryDatabase.getRecentEntries(db, null, helper);
    }

    private static <T extends Entry> T[] getRecentEntries(@NonNull SQLiteDatabase db,
                                                         @Nullable String selection,
                                                         @NonNull EntryHelper<T> helper) {
        T[] returnType = helper.toArray();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_TIME, COLUMN_DATA},
                COLUMN_DATA_TYPE+" = '"+helper.getSqlDataType()+"'"+
                        (selection == null  ? "" : " AND "+selection),
                null,
                null,
                null,
                COLUMN_TIME+" DESC");
        Log.i(TAG, "recent entry cursor length:"+(cursor == null ? "<null>" : cursor.getCount()));
        if(cursor == null || cursor.getCount() == 0) {
            if(returnType.length == 0){
                return  returnType;
            }else{
                return Arrays.copyOf(returnType, 0);
            }
        }
        int timeCol = cursor.getColumnIndex(COLUMN_TIME);
        int dataCol = cursor.getColumnIndex(COLUMN_DATA);

        T[] retVal;//if the provided array is big enough, use that
        if(returnType.length >= cursor.getCount()) {
            retVal = returnType;
        }else{
            retVal = Arrays.copyOf(returnType, cursor.getCount());
        }
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                retVal[i] = helper.from(cursor.getString(dataCol), cursor.getLong(timeCol));
                i++;
            }
            //keep trying to find the most recent BI entry that is valid, until we run out
            catch(NumberFormatException nfe) {
                Log.e(TAG, "Sqlite data is invalid -- new "+helper.getSqlDataType()+" (\""
                        +cursor.getString(dataCol)+"\", "+cursor.getLong(timeCol)+
                        ")  produces \""+nfe.getLocalizedMessage()+"\". Trying next row...");
            }
        }
        cursor.close();

        //due to data validation errors in individual rows, the number of entries returned
        //from n cursor columns is <= n
        //so create a copy of the array without any trailing null values
        List<T> list = new ArrayList<T>();

        for(T s : retVal) {
            if(s != null) {
                list.add(s);
            }
        }

        return list.toArray(Arrays.copyOf(returnType,list.size()));
    }
    @NonNull
    public static QuickActingEntry[] getRecentQA(SQLiteDatabase db) {
        return getRecentEntries(db, FOUR_HOURS_FIVE_MINUTES_MS, QuickActingEntry.HELPER);
/*        long fourHoursFiveMinutesAgo = System.currentTimeMillis() - FOUR_HOURS_FIVE_MINUTES_MS;
        Cursor recentQA = db.query(TABLE_NAME, null,
                COLUMN_TIME+" > "+fourHoursFiveMinutesAgo
                +"AND "+COLUMN_DATA_TYPE+" = "+DATA_QA,
                null, null, null, COLUMN_TIME+" ASC");
        if(recentQA == null || recentQA.getCount() == 0) {
            return new QuickActingEntry[]{};
        }
        QuickActingEntry[] flob = new QuickActingEntry[recentQA.getCount()];
        int i = 0;
        int timeColumn = recentQA.getColumnIndex(COLUMN_TIME);
        int qaColumn = recentQA.getColumnIndex(COLUMN_DATA);
        for(recentQA.moveToFirst(); !recentQA.isAfterLast(); recentQA.moveToNext()) {
            flob[i] = new QuickActingEntry(recentQA.getString(qaColumn), recentQA.getLong(timeColumn));
            i++;
        }
        recentQA.close();
        return flob;*/
    }

    @NonNull
    public static CarbPortionEntry[] getRecentCP(SQLiteDatabase db) {
        return getRecentEntries(db, FOUR_HOURS_FIVE_MINUTES_MS, CarbPortionEntry.HELPER);/*
        long fourHoursFiveMinutesAgo = System.currentTimeMillis() - FOUR_HOURS_FIVE_MINUTES_MS;
        Cursor recentCP = db.query(TABLE_NAME,
                null,
                COLUMN_TIME+" > "+fourHoursFiveMinutesAgo
                + " AND "+COLUMN_DATA_TYPE+" = "+DATA_CP,
                null, null, null,
                COLUMN_TIME+" ASC");
        if(recentCP == null || recentCP.getCount() == 0) {
            return new CarbPortionEntry[]{};
        }
        CarbPortionEntry[] flob = new CarbPortionEntry[recentCP.getCount()];
        int i = 0;
        int time = recentCP.getColumnIndex(COLUMN_TIME);
        int cp = recentCP.getColumnIndex(COLUMN_DATA);
        for(recentCP.moveToFirst(); !recentCP.isAfterLast(); recentCP.moveToNext()) {
            try {
                flob[i] = new CarbPortionEntry(recentCP.getString(cp), recentCP.getLong(time));
                i++;
            }catch(NumberFormatException nfe) {
                Log.e(TAG, "Sqlite data is invalid -- new BackgroundInsulinEntry(\""
                        +recentCP.getString(cp)+"\", "+recentCP.getLong(time)+
                        ")  produces \""+nfe.getLocalizedMessage()+"\". Trying next row...");
            }
        }
        //due to data validation errors in individual rows, the number of entries returned
        //from n cursor columns is <= n
        recentCP.close();
        return flob;*/
    }

    @NonNull
    public static BackgroundInsulinEntry[] getRecentBI(SQLiteDatabase db) {
        return getRecentEntries(db, TWENTY_FIVE_HOURS_MS, BackgroundInsulinEntry.HELPER);/*
        long twentyFourHoursAgo = System.currentTimeMillis() - TWENTY_FIVE_HOURS_MS;
        Cursor recentBI = db.query(TABLE_NAME,
                null,
                COLUMN_TIME+" > "+twentyFourHoursAgo
                +" AND "+COLUMN_DATA_TYPE+" = "+DATA_BI,
                null,
                null,
                null,
                COLUMN_TIME+" DESC");
        Log.i(TAG, "recent BI cursor length:"+(recentBI == null ? "<null>" : recentBI.getCount()));
        if(recentBI == null || recentBI.getCount() == 0) {
            return new BackgroundInsulinEntry[]{};
        }
        int timeColumn = recentBI.getColumnIndex(COLUMN_TIME);
        int biColumn = recentBI.getColumnIndex(COLUMN_DATA);

        BackgroundInsulinEntry[] bie = new BackgroundInsulinEntry[recentBI.getCount()];
        int i = 0;
        for(recentBI.moveToFirst(); !recentBI.isAfterLast(); recentBI.moveToNext()) {
            try {
                bie[i] = new BackgroundInsulinEntry(recentBI.getString(biColumn), recentBI.getLong(timeColumn));
                i++;
            }
            //keep trying to find the most recent BI entry that is valid, until we run out
            catch(NumberFormatException nfe) {
                Log.e(TAG, "Sqlite data is invalid -- new BackgroundInsulinEntry(\""
                        +recentBI.getString(biColumn)+"\", "+recentBI.getLong(timeColumn)+
                        ")  produces \""+nfe.getLocalizedMessage()+"\". Trying next row...");
                //continue;
            }
        }
        recentBI.close();
        return bie;*/
    }

    @Nullable
    public static BloodGlucoseEntry[] getLastBG(SQLiteDatabase db, int numberToGet) {
        BloodGlucoseEntry[] bloodGlucoseEntries = EntryDatabase.getRecentEntries(db,
                BloodGlucoseEntry.HELPER);
        if(numberToGet >= bloodGlucoseEntries.length) {
            return bloodGlucoseEntries;
        } else {
            return Arrays.copyOf(bloodGlucoseEntries, numberToGet);
        }
                /*Cursor lastBG = db.query(TABLE_NAME,
                null,
                COLUMN_DATA_TYPE+" = '"+DATA_BG+"'",
                null,
                null,
                null,
                COLUMN_TIME+" DESC");
        Log.i(TAG, "last BG cursor length:"+(lastBG == null ? "<null>" : lastBG.getCount()));
        if(lastBG == null || lastBG.getCount() == 0) {
            return null;
        }
        int timeColumn = lastBG.getColumnIndex(COLUMN_TIME);
        int bgColumn = lastBG.getColumnIndex(COLUMN_DATA);
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
        return bge;*/
    }

    @Nullable
    private static Entry getMostRecentEntry(SQLiteDatabase db, final String dataType) {
        Cursor entries = db.query(TABLE_NAME,
                null,
                COLUMN_DATA_TYPE+" = "+dataType,
                null,
                null,
                null,
                COLUMN_TIME+" DESC");
        Log.i(TAG, "number of entries in \""+TABLE_NAME+"\": "+
                (entries == null ? "<null>" : entries.getCount()));
        if(entries == null || entries.getCount() == 0) {
            return null;
        }
        int timeColumn = entries.getColumnIndex(COLUMN_TIME);

        //int bgColumn = entries.getColumnIndex(COLUMN_BG);
        entries.moveToFirst();
        Entry entry = null;
        castLoop:
        while(!entries.isAfterLast()) {
            try {
                //bge = new BloodGlucoseEntry(entries.getString(bgColumn), entries.getLong(timeColumn));
                switch (dataType){
                    case DATA_BG:
                        int bgColumn = entries.getColumnIndex(COLUMN_DATA);
                        entry = new BloodGlucoseEntry(entries.getString(bgColumn),
                                entries.getLong(timeColumn));
                        break castLoop;
                    case DATA_CP:
                        int cpColumn = entries.getColumnIndex(COLUMN_DATA);
                        entry = new CarbPortionEntry(entries.getString(cpColumn),
                                entries.getLong(timeColumn));
                        break castLoop;
                    case DATA_QA:
                        int qaColumn = entries.getColumnIndex(COLUMN_DATA);
                        entry = new QuickActingEntry(entries.getString(qaColumn),
                                entries.getLong(timeColumn));
                        break castLoop;
                    case DATA_BI:
                        int biColumn = entries.getColumnIndex(COLUMN_DATA);
                        entry = new BackgroundInsulinEntry(entries.getString(biColumn),
                                entries.getLong(timeColumn));
                        break castLoop;
                    case DATA_KT:
                        int ktColumn = entries.getColumnIndex(COLUMN_DATA);
                        entry = new KetonesEntry(entries.getString(ktColumn),
                                entries.getLong(timeColumn));
                        break castLoop;
                    case DATA_NOTES:
                        int notesColumn = entries.getColumnIndex(COLUMN_DATA);
                        entry = new NotesEntry(entries.getString(notesColumn),
                                entries.getLong(timeColumn));
                        break castLoop;
                }
                break;
            }
            //keep trying to find the most recent entry that is valid, until we run out
            catch(NumberFormatException nfe) {

                Log.e(TAG, "Sqlite data is invalid -- Entry \""
                        +stringOfRow(entries)+"\"  produces \""+nfe.getLocalizedMessage()+"\". Trying next row...");
                entries.moveToNext();
                continue;
            }
        }
        entries.close();
        return entry;
    }


    private static String stringOfRow(Cursor cursor) {
        String ret = "";
        for(int i = 0; i <  cursor.getColumnCount(); i++) {
            String col = (i == 0 ? "" : "; ");
            int type = cursor.getType(i);
            String name = cursor.getColumnName(i);
            col += "\""+name+"\":";
            switch(type) {
                //in descending order of convenience for readability
                case Cursor.FIELD_TYPE_STRING:
                    col+="String="+cursor.getString(i);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    col+="integer="+cursor.getInt(i);
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    col+="float="+cursor.getFloat(i);
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    col+="blob=\""+Bytes.bytesToHex(cursor.getBlob(i))+"\"";
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    col+="null";
                    break;
                default:
                    col+="!UNKNOWN TYPE INT:"+type;
            }
            ret+=col;
        }
        return ret;
    }
}
