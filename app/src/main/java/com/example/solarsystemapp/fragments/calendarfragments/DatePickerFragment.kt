package com.example.solarsystemapp.fragments.calendarfragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.discovernasa.R
import java.util.Calendar

class DatePickerFragment(val listener : (day : String, month : String, year : String) -> Unit)
    : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context, this, year, month, day)
        picker.datePicker.maxDate = calendar.timeInMillis

        return picker
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
       listener(day.toString(), (month+1).toString(), year.toString())
    }

}