package com.medavox.diabeticdiary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * @author Adam Howard
 * @since 25/06/2017
 */
public class DateTimePickerFragment extends DialogFragment {
    private Calendar c = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_time_picker, container);

        final TimePicker timePicker = (TimePicker) (view.findViewById(R.id.timePicker));
        final DatePicker datePicker = (DatePicker) (view.findViewById(R.id.datePicker));

        //disallow selection of dates in the future
        datePicker.setMaxDate(System.currentTimeMillis());
        timePicker.setIs24HourView(true);

        //set pickers to eventInstant
        c.setTimeInMillis(MainActivity.eventInstant);
        datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

        Button confirmButton = (Button) (view.findViewById(R.id.confirm_date_time));
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alternative approach to updating entryTimeButton:
                //view.getRootView().findViewById(R.id.entry_time_button);

                c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                MainActivity.eventInstant = c.getTimeInMillis();
                MainActivity.updateEntryTime((Button) getActivity().findViewById(R.id.entry_time_button));
                DateTimePickerFragment.this.dismiss();
            }
        });
        return view;
    }
}
