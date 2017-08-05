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

/** @author Adam Howard
 * @date 28/07/2017 */
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

    private class CooldownBuncher {
        public boolean hasDataToSend = false;
        public String[] bunchedDataValues;

    }

    /**bunches entries together that occur soon after each other(adds together numeric values),
     * and sends them after a certain time without any further new entries has passed*/
    @Override
    public boolean write(Context c, long time, String[] dataValues) {
        return actualWrite(c,  time, dataValues);
        //todo:
        //regularly check (in another thread) if the time of the last entry is > (30?) seconds ago
        //if/when it is, send the bunched, combined message

        //meanwhile, whenever new data comes in:
            //set time of last entry to this entry time/method call time
            //(how do we account for the time it coming in (time the method was called)
            // being different from the entry time?)
            //(also, if the very first entry in this 'chain' is more than 5 minutes ago,
            //send now, anyway)

            //add the latest entry's data to the bunched data:

                //if the Notes field is  present, and the bunchedData already has a notes field:
                    //send the bunchedData immediately without this latest entry,
                    //and start a new bunch for this latest entry
                //else:
                    //for all the numeric fields (bg,cp,qa,bi,kt), add the value to the running total

    }

    private long timeOfLastEntry;

    public boolean actualWrite(Context c, long time, String[] dataValues) {
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
        if(dataValues[5] != null ) {
            Log.i(TAG, "notes length:"+dataValues[5].length());
        }
        Log.i(TAG, "sms length:"+smsFormatOut.length());
        Log.i(TAG, "sms message: \""+smsFormatOut+"\"");

        //text the entry to interested numbers
        //support runtime permission checks on android versions >= 6.0
        //if we're on android 6+ AND we haven't got location permissions yet, ask for them
        if (Build.VERSION.SDK_INT >= 23 && owner.checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // todo: Show an explanation to the user *asynchronously*
            owner.pendingTextMessage = smsFormatOut;
            owner.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, smsSendRequestCode);
        } else {
            sendSms(smsFormatOut);
        }
        return true;
    }
}
