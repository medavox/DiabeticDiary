package com.medavox.diabeticdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.medavox.diabeticdiary.db.EntryDatabase;
import com.medavox.diabeticdiary.db.entry.BackgroundInsulinEntry;
import com.medavox.diabeticdiary.db.entry.BloodGlucoseEntry;
import com.medavox.diabeticdiary.db.entry.CarbPortionEntry;
import com.medavox.diabeticdiary.db.entry.QuickActingEntry;
import com.medavox.util.io.DateTime;
import static com.medavox.util.io.DateTime.TimeFormat.*;

public class StatusReportActivity extends AppCompatActivity {
    private final static String TAG = "StatusReportActivity";
    private ListView recentQA, recentBI, recentCP;
    private TextView lastBG, cpTotal, qaTotal, biTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_report);
        recentQA = (ListView)findViewById(R.id.recent_qa_value);
        recentBI = (ListView)findViewById(R.id.recent_bi_value);
        recentCP = (ListView)findViewById(R.id.recent_cp_value);
        qaTotal =  (TextView)findViewById(R.id.recent_qa_total);
        cpTotal =  (TextView)findViewById(R.id.recent_cp_total);
        biTotal =  (TextView)findViewById(R.id.recent_bi_total);
        lastBG =   (TextView)findViewById(R.id.last_bg_value);
        //predictedBG = (TextView)findViewById(R.id.predicted_bg_value);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //populate recent cp
        CarbPortionEntry[] cp = EntryDatabase.getRecentCP(EntryDatabase.getReadableDB());
        recentCP.setAdapter(new ArrayAdapter<CarbPortionEntry>(this,
                R.layout.entry_list_item, cp));
        if(cp.length > 0) {
            float total =  0;
            for(CarbPortionEntry cpe : cp) {
                total += cpe.getCarbPortion();
            }
            cpTotal.setText("TOTAL: "+total);
        }
        else {
            cpTotal.setText("TOTAL: 0");
        }

        //populate recent QA
        QuickActingEntry[] qa = EntryDatabase.getRecentQA(EntryDatabase.getReadableDB());
        recentQA.setAdapter(new ArrayAdapter<QuickActingEntry>(this,
                R.layout.entry_list_item, qa));
        if(qa.length > 0) {
            int total =  0;
            for(QuickActingEntry qae : qa) {
                total += qae.getQuickActing();
            }
            qaTotal.setText("TOTAL: "+total);
        }
        else {
            qaTotal.setText("TOTAL: 0");
        }

        //populate recent BI
        BackgroundInsulinEntry[] bi = EntryDatabase.getRecentBI(EntryDatabase.getReadableDB());
        recentBI.setAdapter(new ArrayAdapter<BackgroundInsulinEntry>(this,
                R.layout.entry_list_item, bi));

        if(bi.length > 0) {
            int total =  0;
            for(BackgroundInsulinEntry cpe : bi) {
                total += cpe.getBackgroundInsulin();
            }
            biTotal.setText("TOTAL: "+total);
        }
        else {
            biTotal.setText("TOTAL: 0");
        }

        //populate lastBG field
        BloodGlucoseEntry bg = EntryDatabase.getLastBG(EntryDatabase.getReadableDB());

        if(bg != null) {
            String thag = bg.getBloodGlucose() + " (at " + DateTime.get(bg.getTime(), MINUTES)+")";
            lastBG.setText(thag);
        }
        else {
            Log.w(TAG, "bloodGlucose is null!");
        }
    }
}
