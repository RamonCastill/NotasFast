package com.rrstudio.notasfast

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM NoteEntity")
    fun getAllNotes(): MutableList<NoteEntity>

    @Query("SELECT * FROM NoteEntity where id = :id")
    fun getNoteById(id:Long) : NoteEntity

    @Insert
    fun addNote(noteEntity: NoteEntity): Long

    @Update
    fun updateNote(noteEntity: NoteEntity)

    @Delete
    fun deleteNote(noteEntity: NoteEntity)

}