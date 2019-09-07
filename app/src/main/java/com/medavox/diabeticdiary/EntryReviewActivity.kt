package com.medavox.diabeticdiary

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = LinearLayoutManager(this@EntryReviewActivity)

            // specify an viewAdapter (see also next example)
            adapter = EntryAdapter(resources)
        }
    }

    data class EntryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    inner class EntryAdapter(private val res: Resources)
        : RecyclerView.Adapter<EntryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            TODO("not implemented")
        }

        override fun getItemCount(): Int {
            TODO("not implemented")
        }

        override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
            TODO("not implemented")
        }
    }
}