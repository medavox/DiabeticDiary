package com.medavox.diabeticdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter

import com.medavox.diabeticdiary.db.EntryDatabase
import com.medavox.diabeticdiary.db.entry.BackgroundInsulinEntry
import com.medavox.diabeticdiary.db.entry.BloodGlucoseEntry
import com.medavox.diabeticdiary.db.entry.CarbPortionEntry
import com.medavox.diabeticdiary.db.entry.QuickActingEntry
import kotlinx.android.synthetic.main.activity_status_report.*

class StatusReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_report)
    }

    override fun onResume() {
        super.onResume()

        //populate recent cp
        val cp = EntryDatabase.getRecentCP(EntryDatabase.getReadableDB())
        recent_cp_value.adapter = ArrayAdapter<CarbPortionEntry>(this,
                R.layout.entry_list_item, cp)
        if(cp.isNotEmpty()) {
            var total =  0F
            for(cpe:CarbPortionEntry in cp) {
                total += cpe.carbPortion
            }
            recent_qa_total.text = "TOTAL: $total"
        }
        else {
            recent_qa_total.text = "TOTAL: 0"
        }

        //populate recent QA
        val qa = EntryDatabase.getRecentQA(EntryDatabase.getReadableDB())
        recent_qa_value.setAdapter(ArrayAdapter<QuickActingEntry>(this,
                R.layout.entry_list_item, qa))
        if(qa.isNotEmpty()) {
            var total =  0
            for(qae:QuickActingEntry in qa) {
                total += qae.quickActing
            }
            recent_qa_total.text = "TOTAL: $total"
        }
        else {
            recent_qa_total.text = "TOTAL: 0"
        }

        //populate recent BI
        val bi = EntryDatabase.getRecentBI(EntryDatabase.getReadableDB())
        recent_bi_value.adapter = ArrayAdapter<BackgroundInsulinEntry>(this,
                R.layout.entry_list_item, bi)

        if(bi.isNotEmpty()) {
            var  total =  0
            for(cpe:BackgroundInsulinEntry in bi) {
                total += cpe.backgroundInsulin
            }
            recent_bi_total.text = "TOTAL: $total"
        }
        else {
            recent_bi_total.text = "TOTAL: 0"
        }

        //populate lastBG field
        val bg = EntryDatabase.getLastBG(EntryDatabase.getReadableDB(), 3)
        last_bg_value.adapter = ArrayAdapter<BloodGlucoseEntry>(this, R.layout.entry_list_item, bg)
        /*if(bg != null) {
            String thag = bg.getBloodGlucose() + " (at " + DateTime.get(bg.getTime(), MINUTES)+")"
            lastBG.setText(thag)
        }
        else {
            Log.w(TAG, "bloodGlucose is null!")
        }*/
    }
}
