package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.VIBRATOR_SERVICE;

public class editSubjectDetails extends AppCompatDialogFragment {

    public interface onInput {
        void sendDetailsInput(String status, long date);
    }

    public editSubjectDetails.onInput onInput;
    private CalendarView calendarView;
    private Button button, cancel;
    private RadioButton radioPresent, radioAbsent;
    private String status;
    private String date;
    long dateInMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_edit_details, container, false);

        calendarView = view.findViewById(R.id.calendarID);
        calendarView.setMaxDate(new Date().getTime()+259200000);


        button = view.findViewById(R.id.saveDetails);
        radioAbsent = view.findViewById(R.id.radioAbsent);
        radioPresent = view.findViewById(R.id.radioPresent);
        cancel = view.findViewById(R.id.cancelDetails);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String day = "", mon = "";
                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2);
                dateInMillis = calendar.getTimeInMillis();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                Log.d("day check", "" + dayOfWeek);

                switch (i1) {
                    case 1:
                        mon = "Jan";
                        break;
                    case 2:
                        mon = "Feb";
                        break;
                    case 3:
                        mon = "Mar";
                        break;
                    case 4:
                        mon = "Apr";
                        break;
                    case 5:
                        mon = "May";
                        break;
                    case 6:
                        mon = "Jun";
                        break;
                    case 7:
                        mon = "Jul";
                        break;
                    case 8:
                        mon = "Aug";
                        break;
                    case 9:
                        mon = "Sep";
                        break;
                    case 10:
                        mon = "Oct";
                        break;
                    case 11:
                        mon = "Nov";
                        break;
                    case 12:
                        mon = "Dec";
                        break;
                }

                switch (dayOfWeek) {
                    case 1:
                        day = "Sun";
                        break;
                    case 2:
                        day = "Mon";
                        break;
                    case 3:
                        day = "Tue";
                        break;
                    case 4:
                        day = "Wed";
                        break;
                    case 5:
                        day = "Thu";
                        break;
                    case 6:
                        day = "Fri";
                        break;
                    case 7:
                        day = "Sat";
                        break;
                }
                date = + i2 + " " + mon + " " + i+", "+day;
                Log.d("date check", date);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeItBaby();
                getDialog().dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeItBaby();
                if (date == null) {
                    dateInMillis = new Date().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
                    date = sdf.format(new Date());
                }

                if (radioPresent.isChecked()) {
                    status = "Present";
                    onInput.sendDetailsInput(status, dateInMillis);
                    getDialog().dismiss();
                } else if (radioAbsent.isChecked()) {
                    status = "Absent";
                    onInput.sendDetailsInput(status, dateInMillis);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(),"Select an option",Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInput = (editSubjectDetails.onInput) getActivity();
        } catch (ClassCastException e) {

        }
    }

    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator)getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(125);
        }
    }

}
