package com.medavox.diabeticdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatusReportActivity extends AppCompatActivity {
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

    }
}
