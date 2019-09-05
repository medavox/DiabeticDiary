package com.medavox.diabeticdiary.newdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * @author Adam Howard
@date 14/04/2019
 */
@Database(entities = [Entri::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
