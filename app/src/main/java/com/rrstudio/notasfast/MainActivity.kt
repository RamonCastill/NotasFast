package com.rrstudio.notasfast

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rrstudio.notasfast.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: NoteAdapter
    private lateinit var mGridlayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



        mBinding.fab.setOnClickListener{
            launchEditFragment()
        }

        setupRecyclerView()


    }


    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditNoteFragment()
        if(args != null) fragment.arguments = args
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment) //asiere fragment a la vista main
        fragmentTransaction.addToBackStack(null) //para que vuelava a main al retroceder
        fragmentTransaction.commit() //para aplicar los cambios

        //mBinding.fab.hide() //Aqui se esconde el fab
        hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = NoteAdapter(mutableListOf(), this)
        mGridlayout = GridLayoutManager(this, 2)
        getNotes()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridlayout
            adapter = mAdapter
        }
    }

    private fun getNotes() {
        doAsync {
            val notes = NoteApplication.database.noteDao().getAllNotes()
            uiThread {
                mAdapter.setNote(notes)
            }
        }
    }

    override fun onClick(noteId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), noteId)

        launchEditFragment(args)
    }






    override fun onDeleteNote(noteEntity: NoteEntity) {

        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setPositiveButton(R.string.dialog_delete_confirm,  { dialogInterface, i ->
                    doAsync {
                        NoteApplication.database.noteDao().deleteNote(noteEntity)
                        uiThread {
                            mAdapter.delete(noteEntity)
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_delete_cancel, null)
                .show()

    }

    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()

    }

    override fun addNote(noteEntity: NoteEntity) {
        mAdapter.add(noteEntity)
    }





    override fun updateNote(noteEntity: NoteEntity) {
        mAdapter.update(noteEntity)
    }


    override fun getSupportFragmentManager(): FragmentManager {
        return super.getSupportFragmentManager()
    }




}