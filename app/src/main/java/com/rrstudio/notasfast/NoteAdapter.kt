package com.rrstudio.notasfast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rrstudio.notasfast.databinding.ItemTareaBinding

class NoteAdapter(private var notes:MutableList<NoteEntity>, private var listener: OnClickListener):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTareaBinding.bind(view)

        fun setListener(noteEntity: NoteEntity) {

            with(binding.root) {

                setOnClickListener { listener.onClick(noteEntity.id) }
                true

            }



            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listener.onDeleteNote(noteEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            mContext = parent.context

            val view = LayoutInflater.from(mContext).inflate(R.layout.item_tarea, parent, false)

            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val note = notes.get(position)

            with(holder) {
                setListener(note)

                binding.tvName.text = note.name
                binding.tvDate.text = note.date
                binding.tvDescription.text = note.description
                binding.checkBox.isChecked = false
            }
    }

    override fun getItemCount(): Int = notes.size

    fun add(noteEntity: NoteEntity) {
        if(!notes.contains(noteEntity)) {
            notes.add(noteEntity)
            notifyDataSetChanged()
        }
    }

    fun setNote(notes: MutableList<NoteEntity>){
        this.notes = notes
        notifyDataSetChanged()
    }

    fun update(noteEntity: NoteEntity){
        val index = notes.indexOf(noteEntity)
        if (index != -1){
            notes.set(index, noteEntity)
            notifyItemChanged(index)
        }
    }

    fun delete(noteEntity: NoteEntity){
        val index = notes.indexOf(noteEntity)
        if (index != -1){
            notes.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    }
