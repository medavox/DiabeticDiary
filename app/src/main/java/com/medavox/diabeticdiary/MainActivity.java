package com.medavox.diabeticdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.medavox.diabeticdiary.db.EntryDatabase;
import com.medavox.diabeticdiary.writers.CsvWriter;
import com.medavox.diabeticdiary.writers.DataSink;
import com.medavox.diabeticdiary.writers.SmsWriter;
import com.medavox.diabeticdiary.writers.SqliteWriter;
import com.medavox.util.io.DateTime;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medavox.util.io.DateTime.DateFormat;
import static com.medavox.util.io.DateTime.TimeFormat;
import static com.medavox.util.validate.Validator.check;

//consider importing numberpicker module from https://github.com/SimonVT/android-numberpicker,
//then customising it to my needs

public class MainActivity extends AppCompatActivity {

    public static final String SP_KEY = "Diabetic Diary SharedPreferences Key";
    public static final String ENTRIES_CACHE_KEY = "Diabetic Diary cached entries";
    public static final String SMS_RECIPIENTS_KEY = "Diabetic Diary entry SMS recipients";

    @BindView(R.id.entry_time_button) Button entryTimeButton;

    private static final String TAG = "DiabeticDiary";

    private final int[] inputIDs = new int[] {R.id.BGinput, R.id.CPinput, R.id.QAinput, R.id.BIinput,
            R.id.KTinput, R.id.notesInput};
    private final EditText[] inputs = new EditText[inputIDs.length];
    private final int[] checkboxIDs = new int[] {R.id.BGcheckBox, R.id.CPcheckBox, R.id.QAcheckBox,
            R.id.BIcheckBox, R.id.KTcheckBox, R.id.notesCheckbox};
    private final CheckBox[] checkBoxes = new CheckBox[checkboxIDs.length];

    /**Remembers the SMS message we want to send, when we need to ask permisson first*/
    public String pendingTextMessage = null;
    /**The moment in time that the entry has occured*/
    static long eventInstant;
    private static SmsWriter smsWriter;
    private static DataSink[] outputs;
    private static EntryDatabase entryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //quick sanity check
        try {
            check(inputIDs.length == checkboxIDs.length,
                    "the number of checkboxes and input fields don't match!");
        }
        catch(Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            System.exit(1);
        }

        entryDB = new EntryDatabase(this);

        //initialise writer modules
        smsWriter = new SmsWriter(this);
        outputs = new DataSink[]{new CsvWriter(), smsWriter, new SqliteWriter()};


        for(int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = (CheckBox)findViewById(checkboxIDs[i]);
        }

        //add an anonymous TextWatcher for every input field
        for(int i = 0; i < inputs.length; i++) {
            inputs[i]  = (EditText) findViewById(inputIDs[i]);

            //tick the box when there's text in the input field, and untick it when there's not
            final CheckBox cb = checkBoxes[i];
            inputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c,int i,int j,int k){}
                @Override public void onTextChanged(CharSequence c,int i,int j,int k){}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.length() == 0 && cb.isChecked()) {
                        cb.setChecked(false);
                    }
                    else if(editable.length() > 0 && !cb.isChecked()) {
                        cb.setChecked(true);
                    }
                }
            });
        }

        //for BG input, only allow 2 digits before the decimal place, and 1 after
        //Log.i(TAG, "existing filters: "+inputs[0].getFilters().length);
        inputs[0].setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2,1)});
        //the same with CP
        inputs[1].setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2,1)});

        //for QA, allow no digits after the decimal point.
        inputs[2].setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2,0)});

        //with BI, allow 3 digit integers. I used to take ~80, so it's not impossible
        inputs[3].setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,0)});

        //for KT, I'd be very worried if ketones were > 9.9, but again it's not impossible
        inputs[4].setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2,1)});
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventInstant = System.currentTimeMillis();
        //clear all fields on resume, to prepare the app for a fresh entry
        //clearInputs();
        inputs[0].requestFocus();
        updateEntryTime(entryTimeButton);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_numbers_menu_item:
                DialogFragment newFragment = new EditNumbersDialogFragment();
                newFragment.show(getSupportFragmentManager(), "EditNumbersDialog");
                return true;
            /*case R.id.review_entries_menu_item:
                //todo
                Toast.makeText(this, "Not yet implemented, sorry", Toast.LENGTH_LONG).show();
                return true;*/
            case R.id.status_report_menu_item:
                startActivity(new Intent(this, StatusReportActivity.class));
                return true;
            case R.id.edit_last_entry_menu_item:
                //todo
                Toast.makeText(this, "Not yet implemented, sorry", Toast.LENGTH_LONG).show();
                return true;
            case R.id.carb_calculator_menu_item:
                startActivity(new Intent(this, CarbCalculatorActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.record_button)
    public void clickRecordButton() {
        boolean anyTicked = false;
        for(int i = 0; i < checkBoxes.length; i++) {
            if(checkBoxes[i].isChecked()) {
                anyTicked = true;
            }
        }

        if(!anyTicked) {
            Toast.makeText(this, "No inputs ticked!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] values = new String[inputs.length];
        for(int j = 0; j < inputs.length; j++) {
            if (checkBoxes[j].isChecked()) {
                values[j] = inputs[j].getText().toString();
            } else {
                values[j] = null;
            }
        }

        boolean[] results = new boolean[outputs.length];
        for(int i = 0; i < results.length; i++) {

            results[i] = outputs[i].write(this, eventInstant, values);
        }

        //first cache the entry in SharedPreferences before attempting any writes
        //check which if any datasinks failed to write last time, and get the entry that failed
        //make an extra write() call for the previously failed datasinks, along with this new data
        //... call write() for all datasinks, on this new data

        //for any that failed (returned false), write their toString() to the string array in SharedPrefs,
        //and a copy of the data

        //first cache the entry in SharedPreferences before attempting a disk write
        SharedPreferences sp = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);

        if(sp.contains(ENTRIES_CACHE_KEY)) {
        }
        SharedPreferences.Editor editor = sp.edit();

        //clear the UI fields, ready for another entry
        clearInputs();

        eventInstant++;//add 1ms to the event time, to prevent repeated entry times crashing sqlite
    }

    @OnClick(R.id.entry_time_button)
    public void entryTimeClick() {
        DialogFragment newFragment = new DateTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "DateTimePicker");
    }

    @OnClick(R.id.reset_time_button)
    public void resetTimeClick() {
        eventInstant = System.currentTimeMillis();
        updateEntryTime(entryTimeButton);
    }

    static void updateEntryTime(Button entryTimeButton) {
        if(entryTimeButton != null) {
            entryTimeButton.setText("At " + DateTime.get(eventInstant, TimeFormat.MINUTES,
                    DateFormat.BRIEF_WITH_DAY));
        }
        else {
            Log.e(TAG, "entry time (static) button was null during onResume!");
        }
    }

    //derived from stackoverflow.com/questions/5357455
    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;
        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            String pat="[0-9]{0,"+digitsBeforeZero+"}((\\.[0-9]{0,"+digitsAfterZero+"})|(\\.)?)";
            mPattern=Pattern.compile(pat);
            Log.i(TAG, "pattern:"+pat);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest+source.toString());
            //Log.i(TAG, "dest:"+dest+"; dstart:"+dstart+"; dend:"+dend);
            //Log.i(TAG, "source:"+source+"; start:"+start+"; end:"+end);
            if(!matcher.matches()) {
                return "";
            }
            return null;
        }
    }

    //i really wish i could put this method in SmsWriter
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //if there are no permissions etc to process, return early.
//See https://developer.android.com/reference/android/support/v4/app/ActivityCompat.OnRequestPermissionsResultCallback.html#onRequestPermissionsResult%28int,%20java.lang.String[],%20int[]%29
        if(permissions.length != 1 || grantResults.length != 1) {
            Log.e(TAG, "Got weird permissions results. Permissions length:"+permissions.length+
            "; Grant results length: "+grantResults.length+";  permissions:"+ Arrays.toString(permissions));
            return;
        }

        Log.i(TAG, "permission \"" + permissions[0] + "\" result: " + grantResults[0]);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //if there was a message due to go out when we had to ask permission, send it now
            if(pendingTextMessage != null) {
                smsWriter.sendSms(pendingTextMessage);
                pendingTextMessage = null;
            }
        }
        else {
            Toast.makeText(this, "Permission Refused!", Toast.LENGTH_SHORT).show();
            //permission refused
        }
    }

    private void clearInputs() {
        for(EditText et : inputs) {
            if(et != null) {
                et.setText("");
            }
        }
        for(CheckBox cb : checkBoxes) {
            if(cb != null) {
                cb.setChecked(false);
            }
        }
    }

    //todo: improve validation to include international numbers
    public static boolean isValidPhoneNumber(String s) {
        Pattern pati = Pattern.compile("(0|\\+44)7[0-9]{9}");
        return pati.matcher(s).matches();
        //return s.length() == 11 && s.startsWith("07");
    }
}
