package com.medavox.diabeticdiary

import android.app.Application
import androidx.room.Room
import android.os.Handler
import android.os.HandlerThread
import com.medavox.diabeticdiary.db.AppDb
import com.medavox.diabeticdiary.db.EntryDao

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
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDb::class.java, "Diabetic DB").build()
    }
}