package com.medavox.diabeticdiary.writers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.medavox.diabeticdiary.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class CsvWriter implements DataSink {
    public static final SimpleDateFormat csvDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.UK);
    private static final String TAG = "CSV-Writer";
    @Override
    public boolean write(Context c, long time, String[] dataValues) {

        String csvFormatOut = csvDateFormat.format(new Date(time));

        //select which fields have been ticked
        for(int i = 0; i < dataValues.length; i++) {
            csvFormatOut += ",";
            if(dataValues[i] != null) {
                boolean isNotes = i == dataValues.length-1;
                csvFormatOut += (isNotes ? "\"" : "") + dataValues[i] + (isNotes?"\"":"");
            }
        }

        File storageDir = Environment.getExternalStorageDirectory();
        //save the entry to external storage
        String extState = Environment.getExternalStorageState();
        if(!Environment.MEDIA_MOUNTED.equals(extState)) {
            Log.e(TAG, "external storage is not in a writable state. Actual state: "+extState);
            return false;
        }
        File log = new File(storageDir, "DiabeticDiary.csv");
        try {
            boolean existed = log.exists();
            if(!existed) {
                Log.d(TAG, "csv file does not exist, creating it...");
                if (!log.createNewFile()) {
                    throw new IOException("File.createNewFile returned false for \"" + log + "\"");
                }
            }
            FileOutputStream fos = new FileOutputStream(log, /*append*/true);
            PrintStream csvFile = new PrintStream(fos);

            if(!existed) {
                //write CSV header
                String header = "DATETIME";
                for (String t : dataValues) {
                    header += ","+t;
                }
                //s = s.substring(1);
                csvFile.println(header);
            }
            //file is now ready for writing to, either way
            csvFile.println(csvFormatOut);
            csvFile.close();

            //if we haven't crapped out to the catch-block by now, the diskwrite must have succeeded
            return true;
        }
        catch(IOException ioe) {
            Log.e(TAG, "failed to create file \""+log+"\"; reason: "
                    +ioe.getLocalizedMessage());
            return false;
        }
    }
}
