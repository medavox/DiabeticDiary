package com.medavox.diabeticdiary

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
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

    private lateinit var listView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val uiThreadHandler = Handler(Looper.getMainLooper())
    internal var bottleCommsActive:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_review)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EntryAdapter(resources)
        // Inflate the layout for this fragment
        listView = findViewById<RecyclerView>(R.id.entry_review_list).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    data class EntryViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    inner class EntryAdapter(private val res: Resources)
        : RecyclerView.Adapter<EntryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}