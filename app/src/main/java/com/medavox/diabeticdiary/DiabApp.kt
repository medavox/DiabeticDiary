package com.medavox.diabeticdiary




import android.app.Application
import android.arch.persistence.room.Room
import android.os.Handler
import android.os.HandlerThread
import com.medavox.diabeticdiary.newdb.AppDb

/**
 * @author Adam Howard
@date 15/04/2019
 */
class DiabApp():Application() {
    companion object {
        @JvmField
        var db: AppDb? = null

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