package com.medavox.diabeticdiary

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.date_time_picker.*
import kotlinx.android.synthetic.main.date_time_picker.view.*
import kotlinx.android.synthetic.main.date_time_picker.view.datePicker
import kotlinx.android.synthetic.main.date_time_picker.view.timePicker

import java.util.Calendar

/**
 * @author Adam Howard
 * @since 25/06/2017
 */
class DateTimePickerFragment : DialogFragment() {
    private val c = Calendar.getInstance()

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,
                             savedInstanceState:Bundle?):View? {
        val view = inflater.inflate(R.layout.date_time_picker, container)

        //final TimePicker timePicker = (TimePicker) (view.findViewById(R.id.timePicker))
        //final DatePicker datePicker = (DatePicker) (view.findViewById(R.id.datePicker))

        //disallow selection of dates in the future
        view.datePicker.setMaxDate(System.currentTimeMillis())
        view.timePicker.setIs24HourView(true)

        //set pickers to eventInstant
        c.timeInMillis = MainActivity.eventInstant
        view.datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        view.timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY))
        view.timePicker.setCurrentMinute(c.get(Calendar.MINUTE))

        //Button confirmButton = (Button) (view.findViewById(R.id.))
        view.confirm_date_time.setOnClickListener{
            //alternative approach to updating entryTimeButton:
            //view.getRootView().findViewById(R.id.entry_time_button)

            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                    timePicker.getCurrentHour(), timePicker.getCurrentMinute())
            MainActivity.eventInstant = c.getTimeInMillis()
            MainActivity.updateEntryTime( activity?.entry_time_button)
            this@DateTimePickerFragment.dismiss()
        }
        return view
    }
}
