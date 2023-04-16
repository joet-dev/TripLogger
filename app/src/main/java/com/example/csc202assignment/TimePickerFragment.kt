package com.example.csc202assignment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "time"
private const val ARG_REQUEST_TIME_CODE = "requestTime"
private const val RESULT_TIME_KEY = "resultTime"

/**
 *  TimePickerFragment is used to allow the user to select the trip time.
 *  The fragment manager of the hosting activity uses this class to put the TimePicker DialogFragment on screen.
 *
 *  @author Joseph Thurlow
 */
class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_TIME) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, minute: Int ->

            val result = Bundle().apply {
                putSerializable(RESULT_TIME_KEY,
                    GregorianCalendar(initialYear, initialMonth, initialDay, hour, minute).time)
            }
            parentFragmentManager.setFragmentResult(ARG_REQUEST_TIME_CODE, result)
        }

        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            false
        )
    }
    companion object {
        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, date)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}