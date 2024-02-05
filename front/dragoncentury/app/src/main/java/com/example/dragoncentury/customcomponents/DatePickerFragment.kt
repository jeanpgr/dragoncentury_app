package com.example.dragoncentury.customcomponents

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.dragoncentury.R
import java.util.Calendar

class DatePickerFragment(val listener:(day:Int, month:Int, year:Int) -> Unit): DialogFragment(),
        DatePickerDialog.OnDateSetListener{
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context, R.style.datePickerTheme, this, year, month, day)

        // Personaliza los botones directamente en el DatePickerDialog
        picker.setOnShowListener { dialog ->
            val positiveButton = (dialog as DatePickerDialog).getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_4to))

            // Personaliza el botón negativo (cancelar) si es necesario
            val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_4to))
        }

        return picker
    }
}