package com.medavox.diabeticdiary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medavox.diabeticdiary.databinding.ActivityCarbCalculatorBinding
import com.medavox.diabeticdiary.databinding.ActivityEntryReviewBinding
import com.medavox.diabeticdiary.databinding.EntryReviewItemBinding
import com.medavox.diabeticdiary.db.EntryDao
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * @author Adam Howard
@since 2019-09-06
 */
class EntryReviewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityEntryReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val binding:EntryReviewItemBinding = EntryReviewItemBinding.inflate(LayoutInflater.from(view.context))
            binding.entryValue.text = entryValue
            if(dateHeading != null) {
                binding.dateHeading.text = dateHeading
                binding.dateHeading.visibility = View.VISIBLE
            } else {
                binding.dateHeading.visibility = View.GONE
            }
            if(timeHeading != null) {
                binding.timeHeading.text = timeHeading
                binding.timeHeading.visibility = View.VISIBLE
            } else {
                binding.timeHeading.visibility = View.GONE
            }
            //Log.i("EntryViewHolder", "main thing width:"+view.entry_value.width)
        }
    }

    //todo:
    //  display entries in a table, with each type of Entry in its own column, for easier reading
    //  make entries editable
    //  UI tick-boxes for filtering which type(s) of entries to show
    //  jump to a specific date/time

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

            val currEntryDateTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(entry.time),
                    ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Europe/London"))
            data class showDateAndTime(val showDate:Boolean, val showTime:Boolean)

            val (showDate, showTime) = if(previousEntry == null) {
                showDateAndTime(showDate = true, showTime = true)
            }else {
                val prevEntryDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(previousEntry.time),
                        ZoneOffset.UTC)
                    .withZoneSameInstant(ZoneId.of("Europe/London"))
                showDateAndTime(
                    currEntryDateTime.toLocalDate() != prevEntryDateTime.toLocalDate(),
                    currEntryDateTime.toLocalTime() != prevEntryDateTime.toLocalTime()
                )
            }
            //Log.v(TAG, "entry $position: $entry; showDate=$showDate; showTime=$showTime")

            val dtfDate: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
            val dtfTime:DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val today = LocalDateTime.now(ZoneId.of("Europe/London"))
            val yesterday = today.minusDays(1)
            val dateToDisplay = if (!showDate) null else { with(currEntryDateTime) {
                when {
                    toLocalDate() == today.toLocalDate() -> getString(R.string.today)
                    toLocalDate() == yesterday.toLocalDate() -> getString(R.string.yesterday)
                    //display the year if it's not the same as the current one
                    year != today.year -> currEntryDateTime.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
                    else -> currEntryDateTime.format(dtfDate)
                }
            } }

            val dateTimeConnector = if(dateToDisplay != null) getString(R.string.at_time) else null
            return holder.reuse(
                entryValue = entry.data+" "+entry.entryType.shortName,
                dateHeading = dateToDisplay,
                timeHeading = if(showTime) currEntryDateTime.format(dtfTime) else null
            )
        }
    }
}