package com.medavox.diabeticdiary;

import android.support.annotation.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.realm.RealmObject;
import io.realm.annotations.Required;

import static com.medavox.util.validate.Validator.check;

/**
 * @author Adam Howard
 * @date 07/07/2017
 */

public class Entry  extends RealmObject {
    private static final String TAG ="LogEntry";

    //@Required
    private long timeOccurred;
    private float bg;
    private float cp;
    private int qa;
    private int bi;
    private float kt;
    private String notes;

    private boolean hasBG;
    private boolean hasCP;
    private boolean hasQA;
    private boolean hasBI;
    private boolean hasKT;
    private boolean hasNotes;



    /**Converts back the date-time string from the CSV into a long,
     *  representing milliseconds since the epoch.
     *  Note that the CSV date-time string only records minutes, so some precision is lost.*/
    /*
    public long parseDateTime(String dateTimeString) throws Exception {
        Date det = MainActivity.csvDateFormat.parse(dateTimeString);

        return det.getTime();


        Pattern pat = Pattern.compile("2[0-9]{3}-[0-1][0-9]-[0-3][0-9]_[0-2][0-9]:[0-5][0-9]");
        if(!pat.matcher(dateTimeString).matches()) {
            throw new Exception("argument does not represent a date-time string in the correct format!");
        }
        String[] dateAndTime = dateTimeString.split("_");
        check(dateAndTime.length == 2, "didn't get 2 strings (date and time) when splitting input on '_'. " +
                        "Number of strings:"+dateAndTime.length);
        String date = dateAndTime[0];
        String time = dateAndTime[1];
        String[] dateParts = date.split("-");
        check(dateParts.length == 3, "didn't get 3 strings when splitting date on '-'. " +
                "Number of strings:"+dateParts.length);

    }*/

    private Entry() {
        //validate the string for each entry

        //convert each string into a more appropriate format
    }

    public boolean hasBG() {

    }

    @Nullable
    public Float getBG() {

    }

    public static class Builder {
        private long time;

        String bg;
        String cp;
        String qa;
        String bi;
        String kt;
        String notes;


        public Builder(long time) {
            this.time = time;
        }
        public Builder bg(String bg) {
            this.bg = bg;
            return this;
        }
        public Builder cp(String cp){
            //validate that it's the right format
            this.cp = cp;
            return this;
        }

        public Builder qa(String qa){
            this.qa = qa;
            return this;
        }

        public Builder bi(String bi) {
            this.bi = bi;
            return this;
        }

        public Builder kt(String kt) {
            this.kt = kt;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Entry build() throws NumberFormatException {
            //validate all input field strings, then convert them to floats
            Entry ret = new Entry();
            ret.timeOccurred = time;
            //validate and parse each one

            ret.bg = Float.parseFloat(bg);
        }

        private boolean isValid(String input, int digitsBeforeZero, int digitsAfterZero) {
            String decimalRegex = (digitsAfterZero == 0 ? "" : "\\.[0-9]{0," + digitsAfterZero + "}");
            String pat="[0-9]{0,"+digitsBeforeZero+"}"+decimalRegex;
            Pattern mPattern = Pattern.compile(pat);
            return mPattern.matcher(input).matches();

        }
    }
}
