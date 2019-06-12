package com.relecotech.androidsparsh_smallwonder.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Relecotech on 12-01-2018.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    private Calendar cal;
    private int year;
    private int day;
    private int month;
    private String dob;
    private String value;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        cal = Calendar.getInstance();
        Date dobDate = null;
        System.out.println(" getArguments " + getArguments());
        if (getArguments() != null) {
            dob = getArguments().getString("dob");
            value = getArguments().getString("value");
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                dobDate = format1.parse(dob);
//                cal.setTime(dobDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            System.out.println(" dob " + dob);
        } else {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
        // Create a new instance of DatePickerDialog and return it
        System.out.println("year " + year);
        System.out.println("month " + month);
        System.out.println("day " + day);

        if (value.contains("greater")){
            System.out.println("INSIDE GREATER");
            return new DatePickerDialog(getActivity(), this, year, month, day) {
                @Override
                public void onDateChanged(DatePicker view, int Dyear, int DmonthOfYear, int DdayOfMonth) {
                    if (Dyear < year)
                        view.updateDate(year, month, day);

                    if (DmonthOfYear < month && Dyear == year)
                        view.updateDate(year, month, day);

                    if (DdayOfMonth < day && Dyear == year && DmonthOfYear == month)
                        view.updateDate(year, month, day);

                }
            };
        }else {
            System.out.println("INSIDE LOWER");
            return new DatePickerDialog(getActivity(), this, year, month, day) {
                @Override
                public void onDateChanged(DatePicker view, int Dyear, int DmonthOfYear, int DdayOfMonth) {
                    if (Dyear > year)
                        view.updateDate(year, month, day);

                    if (DmonthOfYear > month && Dyear == year)
                        view.updateDate(year, month, day);

                    if (DdayOfMonth > day && Dyear == year && DmonthOfYear == month)
                        view.updateDate(year, month, day);

                }
            };
        }

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        System.out.println("year " + year);
        System.out.println("month " + month);
        System.out.println("day " + day);

        String dateInString = day + "-" + (month + 1) + "-" + year;

//        Date date = new GregorianCalendar(year,month,day).getTime();
        DateDialogListener activity = (DateDialogListener) getActivity();
        activity.onFinishDialog(dateInString);
//        return dateInString;
    }


    private DatePicker datePicker;

    public interface DateDialogListener {
        void onFinishDialog(String date);
    }
}