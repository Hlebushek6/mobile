package ru.mirea.andreyashkin.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class MyDateDialogFragment extends DialogFragment {
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ((MainActivity)getActivity()).onDateSet(year, month, day);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(), listener, 2026, 2, 25
        );
        return datePickerDialog;
    }
}
