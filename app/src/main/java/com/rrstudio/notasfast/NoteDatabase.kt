package com.rrstudio.notasfast

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(NoteEntity::class), version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}