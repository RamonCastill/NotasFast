package com.rrstudio.notasfast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NoteEntity")
data class NoteEntity(@PrimaryKey(autoGenerate = true) var id:Long = 0,
                      var name: String,
                      var description: String,
                      var date: String ){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
