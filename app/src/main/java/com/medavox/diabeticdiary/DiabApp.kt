package com.medavox.diabeticdiary

import android.app.Application
import android.content.Context
import androidx.room.Room
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.medavox.diabeticdiary.db.AppDb
import com.medavox.diabeticdiary.db.EntryDao
import com.medavox.diabeticdiary.db.Entry
import com.medavox.diabeticdiary.db.EntryType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

/**
 * @author Adam Howard
@date 15/04/2019
 */
class DiabApp():Application() {
    companion object {
        private lateinit var db: AppDb

        fun db():EntryDao {
            return db.entryDao()
        }

        @JvmField
        val dbWorker: Handler

        init {
            val handThread = HandlerThread("DB operations")
            handThread.start()
            dbWorker = Handler(handThread.looper)
        }
    }
    //adapted from https://stackoverflow.com/a/15905916/1655124
    private fun String.csvSplit():Array<String> {
        val words = mutableListOf<String>()
        var quoteMode = false
        var lastSplit=0
        for(i in 0 until length-1) {
            if(this[i] == ',' && !quoteMode) {
                words.add(substring(lastSplit,i).trim('"'))
                lastSplit = i+1
            }
            else if(this[i] == '"') {
                quoteMode = !quoteMode
            }
        }
        words.add(substring(lastSplit))
        return words.toTypedArray()
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)//initialise timezone ID database from Jake Wharton
        db = Room.databaseBuilder(this, AppDb::class.java, "Diabetic DB").
                allowMainThreadQueries().build()
        val sp = getSharedPreferences(getString(R.string.shared_prefs_key), Context.MODE_PRIVATE)
        val TAG = "CSVimport"
        if(sp.getBoolean(getString(R.string.already_imported_csv), false)) {
            Log.i(TAG, "not doing a CSV import, because we've already done it before")
        }else {
            dbWorker.post {
                Log.i(TAG, "importing old DB from CSV...")
                //Toast.makeText(this, "importing old DB from CSV...", Toast.LENGTH_LONG).show()
                //import the old DB; over 8000 entries from the old app
                val reader = BufferedReader(InputStreamReader(assets.open("diabetic-diary.csv")))

                val time = 0
                val id = 1
                val data = 2
                val type = 3
                reader.forEachLine { line ->
                    //val r = Regex(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")
                    val columns = line.csvSplit()
                    try {
                        val importedEntry = Entry(
                                columns[time].toLong(),
                                when (columns[type]) {
                                    "blood_glucose" -> EntryType.BloodGlucose
                                    "carb_portion" -> EntryType.CarbPortion
                                    "quick_acting" -> EntryType.QuickActing
                                    "background_insulin" -> EntryType.BackgroundInsulin
                                    "ketones" -> EntryType.Ketones
                                    "notes" -> EntryType.Notes
                                    else -> EntryType.Unknown
                                },
                                columns[data]
                        )
                        db.entryDao().insertEntries(importedEntry)
                    }catch (e:Exception) {
                        Log.w(TAG, "offending record: $line")
                        throw e
                    }
                }
                Log.i(TAG, "CSV import done!")
                //Toast.makeText(this, "CSV import done!", Toast.LENGTH_LONG).show()
                val editor = sp.edit()
                editor.putBoolean(getString(R.string.already_imported_csv), true)
                editor.apply()
            }
        }
    }
}