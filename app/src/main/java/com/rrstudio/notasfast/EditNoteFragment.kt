package com.rrstudio.notasfast

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.rrstudio.notasfast.databinding.FragmentEditDateBinding
import com.rrstudio.notasfast.databinding.FragmentEditNoteBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EditNoteFragment:  Fragment() {

    private lateinit var mBinding:FragmentEditNoteBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mNoteEntity: NoteEntity? = null
    private lateinit var mEditDate: FragmentEditDateBinding




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentEditNoteBinding.inflate(inflater, container, false)

        mBinding.etDate.setOnClickListener { showDatePickerDialog() }

        return mBinding.root
    }


    private fun showDatePickerDialog() {
        val datePicker = EditDateFragment { day, month, year -> onDateSelected(day, month, year) }



        mActivity?.let { datePicker.show(it?.supportFragmentManager, "datePicker") }
    }



    private fun onDateSelected(day: Int, month: Int, year: Int) {
        mBinding.etDate.setText("$day/${month+1}/$year")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id),0)

        if (id != null && id != 0L){
            mIsEditMode = true
            getNote(id)
        }else{
            mIsEditMode = false
            mNoteEntity = NoteEntity(name = "", date = "", description = "")
        }

        setupActionBar()
        mBinding.etDate.addTextChangedListener { validateFields(mBinding.tilDate) }
        mBinding.etName.addTextChangedListener { validateFields(mBinding.tilName) }
    }

    private fun setupActionBar() {

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_note_title_edit)
                                            else getString(R.string.edit_note_title_add)

        setHasOptionsMenu(true)
    }

    private fun getNote(id: Long) {
        doAsync {
            mNoteEntity =NoteApplication.database.noteDao().getNoteById(id)
            uiThread {
                if (mNoteEntity != null) setUiNote(mNoteEntity!!)
            }
        }
    }

    private fun setUiNote(noteEntity: NoteEntity) {
        with (mBinding){
            etName.text = noteEntity.name.editable()
            etDate.text = noteEntity.date.editable()
            etDescription.text = noteEntity.description.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save ->{
                if (mNoteEntity != null && validateFields(mBinding.tilDate, mBinding.tilName) ){
                    /* val note = NoteEntity(name = mBinding.etName.text.toString().trim(),
                        date = mBinding.etDate.text.toString().trim(),
                        description = mBinding.etDescription.text.toString().trim()) */

                            with(mNoteEntity!!){
                                name = mBinding.etName.text.toString().trim()
                                date = mBinding.etDate.text.toString().trim()
                                description = mBinding.etDescription.text.toString().trim()
                            }

                    doAsync {
                        if(mIsEditMode) NoteApplication.database.noteDao().updateNote(mNoteEntity!!)
                        else  mNoteEntity!!.id = NoteApplication.database.noteDao().addNote(mNoteEntity!!)

                        uiThread {

                            hideKeyboard()

                            if(mIsEditMode){
                                mActivity?.updateNote(mNoteEntity!!)

                                Snackbar.make(mBinding.root, R.string.edit_note_massage_success, Snackbar.LENGTH_SHORT).show()
                            }else {
                                mActivity?.addNote(mNoteEntity!!)

                                Toast.makeText( mActivity, R.string.edit_note_massage_success, Toast.LENGTH_SHORT).show()
                            }

                            mActivity?.onBackPressed()

                        }
                    }

                }
                true
            }

            else-> return super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                textField.editText?.requestFocus()
                isValid = false
            }else textField.error = null
        }

        if(!isValid) Snackbar.make(mBinding.root, R.string.edit_store_message_valid, Snackbar.LENGTH_SHORT).show()

        return isValid
    }



    /* private fun validateFields(): Boolean {
        var isValid = true

        if (mBinding.etDate.text.toString().isEmpty()){
            mBinding.tilDate.error = getString(R.string.helper_required)
            mBinding.etDate.requestFocus()
            isValid = false
        }
        if (mBinding.etName.text.toString().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    } */

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null){
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {

        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }

}