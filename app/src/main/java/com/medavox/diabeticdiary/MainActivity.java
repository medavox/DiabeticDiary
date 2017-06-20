package com.medavox.diabeticdiary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.medavox.util.io.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.medavox.util.validate.Validator.check;
//todo: import numberpicker module from https://github.com/SimonVT/android-numberpicker,
//todo: then customise it to my needs

//todo: re-blank and untick everything during onResume, so we don't have to on every fresh entry

public class MainActivity extends AppCompatActivity {
    private static final int smsSendRequestCode = 42;
    private static final String TAG = "DiabeticDiary";
    private String waitingMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //bg, cp, qa, bi, kt
        final String[] names = new String[] {"BG", "CP", "QA", "BI", "KT"};
        int[] inputIDs = new int[] {R.id.BGinput, R.id.CPinput, R.id.QAinput, R.id.BIinput,
                R.id.KTinput};

        final EditText[] inputs = new EditText[inputIDs.length];
        for(int i = 0; i < inputs.length; i++) {
            inputs[i]  = (EditText) findViewById(inputIDs[i]);
        }

        int[] checkboxIDs = new int[] {R.id.BGcheckBox, R.id.CPcheckBox, R.id.QAcheckBox,
            R.id.BIcheckBox, R.id.KTcheckBox};

        final CheckBox[] checkBoxes = new CheckBox[checkboxIDs.length];
        for(int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = (CheckBox)findViewById(checkboxIDs[i]);
        }

        try {
            check(checkBoxes.length == inputs.length,
                    "the number of names must equal the number of input fields!");
        }
        catch(Exception e) {
            Log.e("DiabeticDiary", "validation exception:"+e);
        }
        Button recordButton = (Button) findViewById(R.id.button);

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

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {
                //get time button was pressed as time of reading
                long now = System.currentTimeMillis();
                //convert to number of 10-second periods (1/6 of a minute) since the epoch
                //this reduces unnecessary precision, and increases the time until we have a Y2K-type issue (in 2038)
                //plus, the time fits within an int, allowing us to use it as the index to an array
                //which can store the log entries
                long hectaMinutes = now/10000;
                //Log.i("DiabeticDiary", "hectaminutes fit within an int:"+ (hectaMinutes < Integer.MAX_VALUE));

                //select which fields have been ticked
                String out = "Diabetic Diary ENTRY @ "+ DateTime.get(now, DateTime.TimeFormat.MINUTES)+" {";
                boolean anyTicked = false;
                for(int i = 0; i < checkBoxes.length; i++) {
                    if(checkBoxes[i].isChecked()){
                        anyTicked = true;
                        out += names[i]+":"+inputs[i].getText()+"; ";
                    }
                }
                out += "}";
                Log.i(TAG, out);

                if(!anyTicked) {
                    Toast.makeText(MainActivity.this, "No inputs ticked!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //support runtime permission checks on android versions >= 6.0
                    //if we're on android 6+ AND we haven't got location permissions yet, ask for them
                    if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {

                        // todo: Show an explanation to the user *asynchronously*
                        // After the user sees the explanation, try again to request the permission.
                        waitingMessage = out;
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, smsSendRequestCode);
                    } else {

                        SmsManager.getDefault().sendTextMessage("redacted", null, out, null, null);
                        Toast.makeText(MainActivity.this, "Message sent:"+out, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    //taken from stackoverflow.com/questions/5357455
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
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        //todo: handle multiple permissions
        //if there are no permissions etc to process, return early.
//See https://developer.android.com/reference/android/support/v4/app/ActivityCompat.OnRequestPermissionsResultCallback.html#onRequestPermissionsResult%28int,%20java.lang.String[],%20int[]%29
        if(permissions.length != 1 || grantResults.length != 1) {
            return;
        }
        Log.i(TAG, "permissions results length:" + permissions.length);


        Log.i(TAG, "permission \"" + permissions[0] + "\" result: " + grantResults[0]);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(waitingMessage != null) {
                SmsManager.getDefault()
                        .sendTextMessage("redacted", null, waitingMessage, null, null);
                Toast.makeText(MainActivity.this, "Message sent:"+waitingMessage,
                        Toast.LENGTH_LONG).show();
                waitingMessage = null;
            }
        }
        else {
            Toast.makeText(this, "Permission Refused!", Toast.LENGTH_SHORT).show();
            //permission refused
        }
    }
}
