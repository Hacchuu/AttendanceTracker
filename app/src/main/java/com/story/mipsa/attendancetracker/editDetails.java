package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class editDetails extends AppCompatDialogFragment {

    public interface onInput{
        void sendDetailsInput(String status, String date);
    }
    public editDetails.onInput onInput;

    CalendarView calendarView;
    Button button,cancel;
    RadioButton radioPresent,radioAbsent;
    String status;
    public String date;
    SubjectDetails subjectDetails;
    ArrayList<AttendanceDetails> attendanceDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_edit_details, container, false);

        calendarView = view.findViewById(R.id.calendarID);
        button = view.findViewById(R.id.saveDetails);
        radioAbsent = view.findViewById(R.id.radioAbsent);
        radioPresent = view.findViewById(R.id.radioPresent);
        cancel = view.findViewById(R.id.cancelDetails);

        subjectDetails = new SubjectDetails();
        attendanceDetails = subjectDetails.getAttendanceDetailsList();
        Log.d("harsh subject array",""+attendanceDetails);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Log.d("harsh check", i+"/"+i1+"/"+i2);

                String day="",mon="";

                Calendar calendar = Calendar.getInstance();
                calendar.set(i,i1,i2);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                Log.d("day check",""+dayOfWeek);

                switch(i1)
                {
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

                switch(dayOfWeek){
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
                date = day+", "+i2+" "+mon+" "+i;
                Log.d("date check", date);
//                String currentDate = sdf.format(calendarView.getDate());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date == null){
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
                    date = sdf.format(new Date());
                }

                if(radioPresent.isChecked()){
                    status = "Present";
                    onInput.sendDetailsInput(status,date);
                }
                else if(radioAbsent.isChecked()){
                    status = "Absent";
                    onInput.sendDetailsInput(status,date);
                }
                else{
                    Toast.makeText(getActivity(), "Select an entry", Toast.LENGTH_SHORT);
                }
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInput = (editDetails.onInput)getActivity();
        }catch (ClassCastException e){

        }
    }

}
