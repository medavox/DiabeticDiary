package com.medavox.diabeticdiary

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medavox.diabeticdiary.databinding.DateTimePickerBinding

import java.util.Calendar

/**
 * @author Adam Howard
 * @since 25/06/2017
 */
class DateTimePickerFragment : DialogFragment() {
    private val c = Calendar.getInstance()
    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,
                             savedInstanceState:Bundle?):View {
        val binding = DateTimePickerBinding.inflate(layoutInflater)
        val view = binding.root

        //final TimePicker timePicker = (TimePicker) (view.findViewById(R.id.timePicker))
        //final DatePicker datePicker = (DatePicker) (view.findViewById(R.id.datePicker))

        //disallow selection of dates in the future
        binding.datePicker.setMaxDate(System.currentTimeMillis())
        binding.timePicker.setIs24HourView(true)

        //set pickers to eventInstant
        c.timeInMillis = (activity as MainActivity?)?.lastEventInstant ?: System.currentTimeMillis()// TODO: viewModel
        binding.datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        binding.timePicker.hour = c.get(Calendar.HOUR_OF_DAY)
        binding.timePicker.minute = c.get(Calendar.MINUTE)

        //Button confirmButton = (Button) (view.findViewById(R.id.))
        binding.confirmDateTime.setOnClickListener{
            //alternative approach to updating entryTimeButton:
            //view.getRootView().findViewById(R.id.entry_time_button)

            c.set(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth,
                binding.timePicker.hour, binding.timePicker.minute
            )
            (activity as MainActivity?)?.updateEntryTime(eventInstant = c.timeInMillis)
            this@DateTimePickerFragment.dismiss()
        }
        return view
    }
}
