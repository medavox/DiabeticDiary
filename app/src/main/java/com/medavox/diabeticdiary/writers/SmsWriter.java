package com.medavox.diabeticdiary.writers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.medavox.diabeticdiary.MainActivity;
import com.medavox.util.io.DateTime;
import com.medavox.util.validate.Validator;

import java.util.Arrays;
import java.util.Set;

/** @author Adam Howard
 * @date 28/07/2017 */
public class SmsWriter implements DataSink {
    private static final int smsSendRequestCode = 42;
    private MainActivity owner;
    private Handler mHandler;
    /**The time to wait until sending bunched data*/
    //private static final int SMS_BUNCH_DELAY = 2 * 60 * 1000;//2 minutes
    private static final int SMS_BUNCH_DELAY = 20 * 1000;//debug: 20 seconds
    /**The time which data must be within now, for it to be bunched*/
    private static final int RECENCY_WINDOW = 50000;
    public SmsWriter(MainActivity activity) {
        this.owner = activity;

        //initialise handler, for use in timeouts
        HandlerThread handThread = new HandlerThread("BLE operations");
        handThread.start();
        mHandler = new Handler(handThread.getLooper());
    }
    private final String[] names = new String[] {"BG", "CP", "QA", "BI", "KT", "NOTES"};
    private final static String TAG = "SMS_Writer";

    //mutable instance variables
    private boolean waitingToSend = false;
    private String[] bunchedValues = new String[names.length];
    private long bunchedTimeSum = 0;
    private int numberOfEntriesBunched = 0;

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

    public void addToBunchedValues(String[] newData) {
        if(newData.length != names.length || newData.length != bunchedValues.length) {
            Log.e(TAG, "String[] passed to SmsWriter.addToBunchedValues() was of incorrect length!" +
                    " expected length: "+bunchedValues.length+"; actual length: "+newData.length+
            "; passed String[] contents: "+Arrays.toString(newData));
            return;
        }
        for(int i = 0; i < newData.length; i++) {
            if(newData[i] != null) {
                if(bunchedValues[i] == null) {
                    bunchedValues[i] = newData[i];
                }
                else {
                    //both the new and incumbent have data for this field,
                    //so we're going to have to combine them numerically (add their number values)
                    try {
                        double incumbentValue = Double.parseDouble(bunchedValues[i]);
                        double newValue = Double.parseDouble(newData[i]);
                        bunchedValues[i] = ""+(incumbentValue+newValue);
                    }
                    catch (NumberFormatException  nfe) {
                        Log.e(TAG, "error while adding strings \""+bunchedValues[i]+"\" and \""+
                                newData[i]+"\" as numbers: "+nfe.getLocalizedMessage());
                    }
                }
            }
        }
    }

    /**If BG, KT or notes has data in both the new and bunched entries,
     * we can't combine them and should send the new data immediately.
     * Otherwise, we can just add CP, QA and BI readings together numerically*/
    private boolean newDataClashesWithBunched(String[] dataValues) {
        try {
            return checkFieldsClash(dataValues, 0) ||
                    checkFieldsClash(dataValues, 4) ||
                    checkFieldsClash(dataValues, 5);
        }
        catch(Exception e) {
            Log.e(TAG, "somehow got an error: "+e.getLocalizedMessage());
        }
        return false;//shouldn't ever get here, but I don't want this exception bubbling up
        //and crashing the whole app/inconveniencing higher-level code with handling it,
        //if it does. It's only meant to be a sanity check!
    }

    private boolean checkFieldsClash(String[] dataValues, int index) throws Exception {
        Validator.check(index >= 0 && index < dataValues.length,
                "index must be in range of String[]. Index value: "+index+"; String[] length: "+
        dataValues.length);
        return (bunchedValues[index] != null
                && dataValues[index] != null);
    }

    /**bunches entries together that occur soon after each other(adds together numeric values),
     * and sends them after a certain time without any further new entries has passed.
     * Entries within 2 minutes of each other with combinable data will be sent as one message*/
    @Override
    public boolean write(Context c, long time, String[] dataValues) {
        //make sure this new incoming data is timestamped to nowish
        //don't want to bunch posthumously written data, and mess up its time
        long timeDifference = Math.abs(System.currentTimeMillis() - time);
        if(timeDifference > RECENCY_WINDOW) {
            Log.w(TAG, "timeDifference: "+timeDifference+"; sending new data immediately...");
            return actualWrite(c,  time, dataValues);
        }
        //if the notes field in this new data isn't null,
        // and we already have data for the notes field saved in bunched values,
        // send now

        if(newDataClashesWithBunched(dataValues)) {
            Log.w(TAG, "fields in old and new data clash, sending new data immediately...");
            return actualWrite(c, time, dataValues);
        }
        //otherwise, do sms data bunching
        if(!waitingToSend) {
            Log.i(TAG, "waiting to send SMS for "+DateTime.getDuration(SMS_BUNCH_DELAY)+
                    ", in case more data comes in");
            final Context ctx = c;
            mHandler.postDelayed(new Runnable() {
                @Override public void run() {
                    //use a time value which is an  average of all the bunched values' times
                    actualWrite(ctx, bunchedTimeSum/numberOfEntriesBunched, bunchedValues);
                    //reset everything
                    bunchedTimeSum = 0;
                    numberOfEntriesBunched = 0;
                    waitingToSend = false;
                }
            }, SMS_BUNCH_DELAY);
            waitingToSend = true;
        }
        addToBunchedValues(dataValues);
        numberOfEntriesBunched++;
        bunchedTimeSum += time;
        Log.i(TAG, "Bunched values now: "+Arrays.toString(bunchedValues));
        return true;
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
