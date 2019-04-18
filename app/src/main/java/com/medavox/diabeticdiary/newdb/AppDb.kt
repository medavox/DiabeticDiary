package com.medavox.diabeticdiary.newdb

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

/**
 * @author Adam Howard
@date 14/04/2019
 */
@Database(entities = [Entri::class], version = 2)
@TypeConverters(TypeConverters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}