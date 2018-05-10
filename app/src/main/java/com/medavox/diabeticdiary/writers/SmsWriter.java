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

    private static final String LAST_HYPO_KEY = "time of last hypo";
    private static final long HYPO_ALERT_PERIOD = 30 * 60 * 1000;//30 minutes

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

    /**Only actually sends an SMS if any of the following are true:<br />
     * <ul>
     * <li>A BG reading of < 5.0 has been recorded in the last {@link #HYPO_ALERT_PERIOD} time;</li>
     * <li>The entry data contains a BG Reading of >= 12.0 </li>
     * <li>The entry contains either ketone or Background Insulin data</li></ul>*/
    @Override
    public boolean write(Context c, long time, String[] dataValues) {
        SharedPreferences sp = c.getSharedPreferences(MainActivity.SP_KEY, Context.MODE_PRIVATE);
        String bgReading = dataValues[MainActivity.INDEX_BG];

        long lastHypoTime = sp.getLong(LAST_HYPO_KEY, -1);
        boolean shouldSendThisText = false;
        if(lastHypoTime+HYPO_ALERT_PERIOD > time) {
            //there's been a low BG reading in the last HYPO_ALERT_PERIOD ms (currently 30 mins);
            //send the text
            shouldSendThisText = true;
        }
        if(bgReading != null && bgReading.length() > 0) {
            try {
                float bgAsNumber = Float.valueOf(bgReading);
                SharedPreferences.Editor editor = sp.edit();
                if(bgAsNumber <= 5.0) {
                    //bg reading is low; send the text and ACTIVATE HYPO MODE!
                    editor.putLong(LAST_HYPO_KEY, time);
                    editor.apply();
                    shouldSendThisText = true;
                }
                if(bgAsNumber >= 12.0) {
                    //bg is high; send the text
                    shouldSendThisText = true;
                }
                if(bgAsNumber > 5.5) {
                    //no longer having a hypo; send this normal BG reading,
                    //if we're within a hypo alert period,
                    // cancel the hypo alert period
                    //DEACTIVATE HYPO MODE!
                    editor.putLong(LAST_HYPO_KEY, -1);
                    editor.apply();

                }
            }catch (NumberFormatException nfe) {
                Log.e(TAG, "failed to parse BG reading \""+bgReading+"\" as a float!");
            }
        }

        //if the entry contains any ketone, BI or notes, send a text
        String kt = dataValues[MainActivity.INDEX_KT];
        String bi = dataValues[MainActivity.INDEX_BI];
        if((kt != null && kt.length() > 0)
                || (bi != null && bi.length() > 0)) {
            shouldSendThisText = true;
        }

        if(!shouldSendThisText){
            //if we're not in hypo mode, don't send texts about CP, BG or anything else
            Log.i(TAG, "Nothing urgent in this entry; not sending SMS");
            Toast.makeText(owner, "No hypo in progress; not sending SMS",
                    Toast.LENGTH_LONG).show();
            return true;
        }
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
