package com.medavox.diabeticdiary.db

import android.content.Context
import android.content.SharedPreferences
import com.medavox.diabeticdiary.DiabApp
import com.medavox.diabeticdiary.writers.DataSink

/**
 * @author Adam Howard
@date 16/04/2019
 */
class SqliteWriter : DataSink {
    override fun write(sp: SharedPreferences, time: Long, dataValues: Map<EntryType, String>): Boolean {
        DiabApp.dbWorker.post {
            val entris = dataValues.map { Entry(time, it.key, it.value) }
            DiabApp.db().insertEntries(entris)
        }
        return true
    }
}