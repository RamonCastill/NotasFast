package com.rrstudio.notasfast

interface OnClickListener {
    fun onClick(noteId: Long)
    fun onDeleteNote(noteEntity: NoteEntity)
}