package com.medavox.diabeticdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.medavox.diabeticdiary.db.EntryDatabase;
import com.medavox.diabeticdiary.db.entry.BloodGlucoseEntry;
import com.medavox.util.io.DateTime;
import static com.medavox.util.io.DateTime.TimeFormat.*;

public class StatusReportActivity extends AppCompatActivity {
    private final static String TAG = "StatusReportActivity";
    private TextView recentQA;
    private TextView recentCP;
    private TextView lastBG;
    private TextView sugarSwing;
    private TextView predictedBG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_report);
        recentQA = (TextView)findViewById(R.id.recent_qa_value);
        recentCP = (TextView)findViewById(R.id.recent_cp_value);
        lastBG = (TextView)findViewById(R.id.last_bg_value);
        sugarSwing = (TextView)findViewById(R.id.cp_qa_swing_value);
        predictedBG = (TextView)findViewById(R.id.predicted_bg_value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //populate lastBG field
        BloodGlucoseEntry bg = EntryDatabase.getLastBG(EntryDatabase.getReadableDB());
        Log.i(TAG, "bloodGlucose is null:"+(bg==null));
        if(bg != null) {
            String thag = bg.getBloodGlucose() + " (at " + DateTime.get(bg.getTime(), MINUTES)+")";
            lastBG.setText(thag);
        }
    }
}
