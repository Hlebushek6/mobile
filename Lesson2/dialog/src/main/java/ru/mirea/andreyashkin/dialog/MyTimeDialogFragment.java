package ru.mirea.andreyashkin.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class MyTimeDialogFragment extends DialogFragment {

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                ((MainActivity)getActivity()).onTimeSet(hour, minute);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(), listener, 9, 0, true
        );
        return timePickerDialog;
    }
}
