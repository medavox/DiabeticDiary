package com.medavox.diabeticdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatusReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_report);
        TextView recentQA = (TextView)findViewById(R.id.recent_qa_value);
        TextView recentCP = (TextView)findViewById(R.id.recent_cp_value);
        TextView lastBG = (TextView)findViewById(R.id.last_bg_value);
        TextView sugarSwing = (TextView)findViewById(R.id.cp_qa_swing_value);
        TextView predictedBG = (TextView)findViewById(R.id.predicted_bg_value);


    }
}
