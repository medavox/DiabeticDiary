package com.medavox.diabeticdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.medavox.diabeticdiary.db.Entry
import com.medavox.diabeticdiary.db.EntryType

import kotlinx.android.synthetic.main.activity_status_report.*

class StatusReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_report)
    }

    override fun onResume() {
        super.onResume()

        //populate recent cp
        val cp = DiabApp.db().getRecentCP(Entry.fiveHoursAgo)
        recent_cp_value.adapter = ArrayAdapter<Entry>(this,
                R.layout.status_report_entry_list_item, cp)
        if(cp.isNotEmpty()) {
            check(cp.all { it.entryType == EntryType.CarbPortion })//check they're all CP entries
            val total = cp.fold(0F ){acc:Float, elem:Entry -> acc+elem.data.toFloat()}
            recent_qa_total.text = "TOTAL: $total"
        }
        else {
            recent_qa_total.text = "TOTAL: 0"
        }

        //populate recent QA
        val qa = DiabApp.db().getRecentQA(Entry.fiveHoursAgo)
        recent_qa_value.setAdapter(ArrayAdapter<Entry>(this,
                R.layout.status_report_entry_list_item, qa))
        if(qa.isNotEmpty()) {
            check(qa.all { it.entryType == EntryType.QuickActing })//check they're all QA entries
            val total =  qa.fold(0) { acc:Int, elem:Entry -> acc + elem.data.toInt()}
            recent_qa_total.text = "TOTAL: $total"
        }
        else {
            recent_qa_total.text = "TOTAL: 0"
        }

        //populate recent BI
        val bi = DiabApp.db().getRecentBI(Entry.twentyFiveHoursAgo)
        recent_bi_value.adapter = ArrayAdapter<Entry>(this,
                R.layout.status_report_entry_list_item, bi)

        if(bi.isNotEmpty()) {
            check(bi.all { it.entryType == EntryType.BackgroundInsulin })//check they're all BI entries
            val  total = bi.fold(0) {acc:Int, elem:Entry -> acc + elem.data.toInt()}
            recent_bi_total.text = "TOTAL: $total"
        }
        else {
            recent_bi_total.text = "TOTAL: 0"
        }

        //populate lastBG field
        val bg = DiabApp.db().getLastBG(3)
        last_bg_value.adapter = ArrayAdapter<Entry>(this, R.layout.status_report_entry_list_item, bg)
        /*if(bg != null) {
            String thag = bg.getBloodGlucose() + " (at " + DateTime.get(bg.getTime(), MINUTES)+")"
            lastBG.setText(thag)
        }
        else {
            Log.w(TAG, "bloodGlucose is null!")
        }*/
    }
}
