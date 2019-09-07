package com.medavox.diabeticdiary.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * @author Adam Howard
@date 14/04/2019
 */
@Database(entities = [Entry::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
