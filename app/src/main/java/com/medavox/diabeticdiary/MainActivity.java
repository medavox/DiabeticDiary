package com.medavox.diabeticdiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import com.medavox.util.io.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medavox.util.io.DateTime.TimeFormat;
import static com.medavox.util.io.DateTime.DateFormat;
import static com.medavox.util.validate.Validator.check;

//consider importing numberpicker module from https://github.com/SimonVT/android-numberpicker,
//then customising it to my needs

public class MainActivity extends AppCompatActivity {

    public static final String SP_KEY = "Diabetic Diary SharedPreferences Key";
    private static final String ENTRIES_CACHE_KEY = "Diabetic Diary cached entries";
    public static final String SMS_RECIPIENTS_KEY = "Diabetic Diary entry SMS recipients";

    @BindView(R.id.entry_time_button) Button entryTimeButton;

    private static final int smsSendRequestCode = 42;
    private static final String TAG = "DiabeticDiary";

    private final String[] names = new String[] {"BG", "CP", "QA", "BI", "KT", "NOTES"};
    private final int[] inputIDs = new int[] {R.id.BGinput, R.id.CPinput, R.id.QAinput, R.id.BIinput,
            R.id.KTinput, R.id.notesInput};
    private final EditText[] inputs = new EditText[inputIDs.length];
    private final int[] checkboxIDs = new int[] {R.id.BGcheckBox, R.id.CPcheckBox, R.id.QAcheckBox,
            R.id.BIcheckBox, R.id.KTcheckBox, R.id.notesCheckbox};
    private final CheckBox[] checkBoxes = new CheckBox[checkboxIDs.length];

    private String waitingMessage = null;
    static long instantOpened;
    private File storageDir;
    public static final SimpleDateFormat csvDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.UK);

    //the timestamp , andevery field (excluding NOTES) at its max length, in the SMS format
    private static final int MAX_CHARS = 62;

    //including ( NOTES:"<MSG>") makes 71

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //quick sanity check
        try {
            check(names.length == inputIDs.length && names.length == checkboxIDs.length,
                    "the number of checkboxes, input fields and names don't match!");
        }
        catch(Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            System.exit(1);
        }

        for(int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = (CheckBox)findViewById(checkboxIDs[i]);
        }

        //add an anonymous TextWatcher for every input field
        for(int i = 0; i < inputs.length; i++) {
            inputs[i]  = (EditText) findViewById(inputIDs[i]);

            //tick the box when there's text in the input field, and untick it when there's not
            final CheckBox cb = checkBoxes[i];
            inputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c, int i, int j, int k){}
                @Override public void onTextChanged(CharSequence c, int i, int j, int k){}

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

        //for KT, I'd be very worried if ketones were > 9, but again it's not impossible
        inputs[4].setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2,1)});

        storageDir = Environment.getExternalStorageDirectory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        instantOpened  = System.currentTimeMillis();
        //clear all fields on resume, to prepare the app for a fresh entry
        clearInputs();
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
            case R.id.review_entries_menu_item:
                //todo
                Toast.makeText(this, "Not yet implemented, sorry", Toast.LENGTH_LONG).show();
                return true;
            case R.id.status_report_menu_item:
                startActivity(new Intent(this, StatusReportActivity.class));
                return true;
            case R.id.edit_last_entry_menu_item:
                //todo
                Toast.makeText(this, "Not yet implemented, sorry", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.record_button)
    public void clickRecordButton() {
        //generate the csv-format log line, store it in SharePreferences,
        //then attempt to write it to external storage.

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.UK);
        String csvFormatOut = csvDateFormat.format(new Date(instantOpened));

        //select which fields have been ticked
        String smsFormatOut = DateTime.get(instantOpened,
                DateTime.TimeFormat.MINUTES, DateFormat.BRIEF_WITH_DAY)+": ";
        boolean anyTicked = false;
        for(int i = 0; i < checkBoxes.length; i++) {
            csvFormatOut += ",";
            if(checkBoxes[i].isChecked()) {
                anyTicked = true;
                smsFormatOut += names[i]+":"+inputs[i].getText()+", ";
                boolean isNotes = names[i].equals("NOTES");
                csvFormatOut += (isNotes ? "\"" : "") + inputs[i].getText() + (isNotes?"\"":"");
            }
        }
        if(smsFormatOut.endsWith(", ")) {
            smsFormatOut = smsFormatOut.substring(0, smsFormatOut.length()-2);
        }
        //csvFormatLine = csvFormatLine.substring(1);
        Log.i(TAG, smsFormatOut);
        Log.i(TAG, "notes length:"+inputs[5].getText().length());
        Log.i(TAG, "sms length:"+smsFormatOut.length());

        if(!anyTicked) {
            Toast.makeText(MainActivity.this, "No inputs ticked!", Toast.LENGTH_SHORT).show();
        }
        else {
            //first cache the entry in SharedPreferences before attempting a disk write
            SharedPreferences sp = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);

            if(sp.contains(ENTRIES_CACHE_KEY)) {
                //prepend existing data to what we'll write to SP
                csvFormatOut = sp.getString(ENTRIES_CACHE_KEY, "") + "\n" + csvFormatOut;
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ENTRIES_CACHE_KEY, csvFormatOut);
            editor.apply();

            //save the entry to external storage
            String extState = Environment.getExternalStorageState();
            if(!Environment.MEDIA_MOUNTED.equals(extState)) {
                Log.e(TAG, "external storage is not in a writable state. Actual state: "+extState);
            }
            else {
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
                        String s = "DATETIME";
                        for (String t : names) {
                            s += ","+t;
                        }
                        //s = s.substring(1);
                        csvFile.println(s);
                    }
                    //file is now ready for writing to, either way
                    csvFile.println(csvFormatOut);
                    csvFile.close();

                    //if we haven't crapped out to the catch-block by now, the diskwrite must have succeeded
                    //so delete the cached entries in SP
                    editor.remove(ENTRIES_CACHE_KEY);
                    editor.apply();
                }
                catch(IOException ioe) {
                    Log.e(TAG, "failed to create file \""+log+"\"; reason: "
                            +ioe.getLocalizedMessage());
                }
            }


            //text the entry to interested numbers
            //support runtime permission checks on android versions >= 6.0
            //if we're on android 6+ AND we haven't got location permissions yet, ask for them
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // todo: Show an explanation to the user *asynchronously*
                waitingMessage = smsFormatOut;
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, smsSendRequestCode);
            } else {
                sendSms(smsFormatOut);
            }

            //clear the fields, ready for another entry
            clearInputs();
        }
    }

    @OnClick(R.id.entry_time_button)
    public void entryTimeClick() {
        DialogFragment newFragment = new DateTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "DateTimePicker");
    }

    @OnClick(R.id.reset_time_button)
    public void resetTimeClick() {
        instantOpened = System.currentTimeMillis();
        updateEntryTime(entryTimeButton);
    }

    static void updateEntryTime(Button entryTimeButton) {
        if(entryTimeButton != null) {
            entryTimeButton.setText("At " + DateTime.get(instantOpened, TimeFormat.MINUTES,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //if there are no permissions etc to process, return early.
//See https://developer.android.com/reference/android/support/v4/app/ActivityCompat.OnRequestPermissionsResultCallback.html#onRequestPermissionsResult%28int,%20java.lang.String[],%20int[]%29
        if(permissions.length != 1 || grantResults.length != 1) {
            Log.e(TAG, "Got weird permissions results. Permissions length:"+permissions.length+
            "; Grant results length: "+grantResults.length);
            return;
        }
        Log.i(TAG, "permissions results length:" + permissions.length);


        Log.i(TAG, "permission \"" + permissions[0] + "\" result: " + grantResults[0]);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //if there was a message due to go out when we had to ask permission, send it now
            if(waitingMessage != null) {
                sendSms(waitingMessage);
                waitingMessage = null;
            }
        }
        else {
            Toast.makeText(this, "Permission Refused!", Toast.LENGTH_SHORT).show();
            //permission refused
        }
    }

    /**Sends the text to all interested phone numbers*/
    private void sendSms(String message) {
        SharedPreferences sp = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        Set<String> defaultNumbers = new LinkedHashSet<>(0);
        Set<String> recipients = sp.getStringSet(SMS_RECIPIENTS_KEY, defaultNumbers);
        if(recipients.size() > 0) {
            for(String number : recipients) {
                if(isValidPhoneNumber(number)) {
                    SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
                }
                else {
                    Log.e(TAG, "invalid phone number \""+number+"\" in SharedPreferences");
                }
            }

            Toast.makeText(MainActivity.this, "SMS sent:\"" + message + "\"",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(MainActivity.this, "No recipients set for SMS entry sending",
                    Toast.LENGTH_LONG).show();
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

    public static String stringsOf(Collection<Object> co) {
        String s = "[ ";
        for(Object o : co) {
            s += o.toString()+"; ";
        }
        return s+" ]";
    }

    public static String stringsOf(Object[] array) {
        String s = "[ ";
        for(int i = 0; i < array.length; i++) {
            s += array[i].toString()+"; ";
        }
        return s+" ]";
    }

    //todo: improve validation to include international numbers
    public static boolean isValidPhoneNumber(String s) {
        Pattern pati = Pattern.compile("(0|\\+44)7[0-9]{9}");
        return pati.matcher(s).matches();
        //return s.length() == 11 && s.startsWith("07");
    }
}
