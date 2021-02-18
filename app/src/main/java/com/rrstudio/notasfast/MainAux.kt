package com.rrstudio.notasfast

interface MainAux {

    fun hideFab(isVisible: Boolean = false)

    fun addNote(noteEntity: NoteEntity)

    fun updateNote(noteEntity: NoteEntity)

}