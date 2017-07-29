package com.medavox.diabeticdiary.writers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.medavox.diabeticdiary.MainActivity;
import com.medavox.util.io.DateTime;

import java.util.Set;

/**
 * @author Adam Howard
 * @date 28/07/2017
 */

public class SmsWriter implements DataSink {
    private static final int smsSendRequestCode = 42;
    private MainActivity owner;
    public SmsWriter(MainActivity activity) {
        this.owner = activity;
    }
    private final String[] names = new String[] {"BG", "CP", "QA", "BI", "KT", "NOTES"};
    private final static String TAG = "SMS_Writer";

    /**Sends the text to all interested phone numbers*/
    public void sendSms(String message) {
        SharedPreferences sp = owner.getSharedPreferences(MainActivity.SP_KEY, Context.MODE_PRIVATE);
        Set<String> recipients = sp.getStringSet(MainActivity.SMS_RECIPIENTS_KEY, null);
        if(recipients != null && recipients.size() > 0) {
            for(String number : recipients) {
                if(MainActivity.isValidPhoneNumber(number)) {
                    SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
                }
                else {
                    Log.e(TAG, "invalid phone number \""+number+"\" in SharedPreferences");
                }
            }

            Toast.makeText(owner, "SMS sent:\"" + message + "\"",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(owner, "No recipients set for SMS entry sending",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    //todo:move runtime permissions code from MainActivity to this class
    public boolean write(Context c, long time, String[] dataValues) {

        //select which fields have been ticked
        String smsFormatOut = DateTime.get(time,
                DateTime.TimeFormat.MINUTES, DateTime.DateFormat.BRIEF_WITH_DAY)+": ";
        for(int i = 0; i < dataValues.length; i++) {
            if(dataValues[i] != null) {
                smsFormatOut += names[i]+":"+dataValues[i]+", ";
            }
        }
        if(smsFormatOut.endsWith(", ")) {
            smsFormatOut = smsFormatOut.substring(0, smsFormatOut.length()-2);
        }
        //csvFormatLine = csvFormatLine.substring(1);
        Log.i(TAG, smsFormatOut);
        Log.i(TAG, "notes length:"+dataValues[5].length());
        Log.i(TAG, "sms length:"+smsFormatOut.length());

        //text the entry to interested numbers
        //support runtime permission checks on android versions >= 6.0
        //if we're on android 6+ AND we haven't got location permissions yet, ask for them
        if (Build.VERSION.SDK_INT >= 23 && owner.checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // todo: Show an explanation to the user *asynchronously*
            owner.waitingMessage = smsFormatOut;
            owner.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, smsSendRequestCode);
        } else {
            sendSms(smsFormatOut);
        }
        return true;
    }
}
