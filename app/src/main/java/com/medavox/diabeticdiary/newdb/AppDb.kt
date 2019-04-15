package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * @author Adam Howard
@date 14/04/2019
 */
@Database(entities = [Entri::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
