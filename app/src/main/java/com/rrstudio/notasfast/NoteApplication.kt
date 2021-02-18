package com.rrstudio.notasfast

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class NoteApplication: Application() {

    companion object{
        lateinit var database: NoteDatabase

    }

    override fun onCreate() {
        super.onCreate()



        database = Room.databaseBuilder(this,
                NoteDatabase::class.java,
                "NoteDatabase").build()
    }

}