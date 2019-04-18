package com.medavox.diabeticdiary.newdb

import android.content.Context
import com.medavox.diabeticdiary.DiabApp
import com.medavox.diabeticdiary.writers.DataSank

/**
 * @author Adam Howard
@date 16/04/2019
 */
class NewSqliteWriter : DataSank {
    override fun write(c: Context, time: Long, dataValues: Map<EntryType, String>): Boolean {
        val db = DiabApp.db
        db?.let {
            DiabApp.dbWorker.post {
                val entris = dataValues.map { Entri(time, it.key, it.value) }
                db.entryDao().insertEntries(entris)
            }
        }
        return true
    }
}