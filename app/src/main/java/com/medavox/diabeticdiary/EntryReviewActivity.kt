package com.medavox.diabeticdiary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medavox.diabeticdiary.db.EntryDao
import kotlinx.android.synthetic.main.entry_review_item.view.*
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author Adam Howard
@since 2019-09-06
 */
class EntryReviewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_review)

        // Inflate the layout for this fragment
        recyclerView = findViewById<RecyclerView>(R.id.entry_review_list).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(false)

            // use a linear layout manager
            layoutManager = LinearLayoutManager(this@EntryReviewActivity).apply{reverseLayout = true}

            // specify an viewAdapter (see also next example)
            adapter = EntryAdapter(DiabApp.db())
        }
    }

    data class EntryViewHolder(val view:View) : RecyclerView.ViewHolder(view){
        fun reuse(entryValue:String, dateHeading:String?=null, timeHeading:String?=null) {
            view.entry_value.text = entryValue
            if(dateHeading != null) {
                view.date_heading.text = dateHeading
                view.date_heading.visibility = View.VISIBLE
            } else {
                view.date_heading.visibility = View.GONE
            }
            if(timeHeading != null) {
                view.time_heading.text = timeHeading
                view.time_heading.visibility = View.VISIBLE
            } else {
                view.time_heading.visibility = View.GONE
            }
        }
    }

    //Use those context-establishing time/date headings (like on slack/fbmsgr)
    // preceding each entry:
    // 1) before the first entry on a date different to the previous entry, and
    // 2) on a different time (hour and minute) to the previous entry
    // considering we don't control individual Views placement (the RecyclerView does),
    // implement this by including the date or time heading as part of that entry with the different date/time
    inner class EntryAdapter(private val dao:EntryDao): RecyclerView.Adapter<EntryViewHolder>() {
        private val TAG = "EntryAdapter"
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.entry_review_item, parent, false)
            return EntryViewHolder(v)
        }

        override fun getItemCount(): Int {
            val num = dao.getNumberOfEntries()
            //Log.v(TAG, "entries: $num")
            return num
        }

        override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
            val entry = dao.getNthMostRecentEntry(position)
            if(entry == null) {
                Log.e(TAG, "entry $position is null!")
                return
            }
            val previousEntry = dao.getNthMostRecentEntry(position+1)

            val currentDateTime = ZonedDateTime.ofInstant(Instant.
                    ofEpochMilli(entry.time), ZoneOffset.UTC)
            data class showDateAndTime(val showDate:Boolean, val showTime:Boolean)

            val (showDate, showTime) = if(previousEntry == null) {
                showDateAndTime(showDate = true, showTime = true)
            }else {
                val previousDateTime = ZonedDateTime.ofInstant(Instant.
                        ofEpochMilli(previousEntry.time), ZoneOffset.UTC)
                showDateAndTime(
                currentDateTime.toLocalDate() != previousDateTime.toLocalDate(),
                currentDateTime.toLocalTime() != previousDateTime.toLocalTime()
                )
            }
            //ZoneId.of("Europe/London")
            Log.v(TAG, "entry $position: $entry; showDate=$showDate; showTime=$showTime")
            val dtfDate:DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
                    .withZone(ZoneId.of("Europe/London"))
            val dtfTime:DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneId.of("Europe/London"))
            val today = LocalDateTime.now(ZoneOffset.UTC)
            val yesterday = today.minusDays(1)
            val dateToDisplay = if(showDate) {
                when (currentDateTime.toLocalDate()) {
                    today.toLocalDate() -> getString(R.string.today)
                    yesterday.toLocalDate() -> getString(R.string.yesterday)
                    else -> currentDateTime.format(dtfDate)
                }
            } else null

            val dateTimeConnector = if(dateToDisplay != null) getString(R.string.at_time) else null
            return holder.reuse(
                entryValue = entry.data+" "+entry.entryType.shortName,
                dateHeading = dateToDisplay,
                timeHeading = if(showTime) currentDateTime.format(dtfTime) else null
            )
        }
    }
}