package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class AttendanceTarget extends AppCompatActivity {
    TextView textView;
    Button button;
    SeekBar seekBar;
    public static String minimumAttendance;
    FirstPage firstPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_target);

        textView = (TextView) findViewById(R.id.number);
        button = (Button)findViewById(R.id.save);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int x;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                x = i;
                textView.setText(x+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                textView.setText(minimumAttendance+"%");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                minimumAttendance = Integer.toString(x);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                minimumAttendance = textView.getText().toString().trim();

                Toast message = Toast.makeText(getApplicationContext(),"Minimum Attendance set to "+minimumAttendance+"%",Toast.LENGTH_SHORT);
                View toastView = message.getView();
                toastView.setBackgroundResource(R.drawable.toast_color);
                TextView v = (TextView) message.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.BLACK);
                message.show();



//                DbManager dbManager = new DbManager(getApplicationContext());
//                int count = dbManager.getCount();
//
//                if(count == 0) {
//                    String res = dbManager.addRecord(firstPage.name, minimumAttendance, 1);
//                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
//
//                                    }
//                else {
//                    String res2 = dbManager.updateRecordTarget( minimumAttendance, 1);
//                    if (res2 == "Success")
//                        Toast.makeText(getApplicationContext(), res2, Toast.LENGTH_LONG).show();
//                    else
//                        Toast.makeText(getApplicationContext(), res2, Toast.LENGTH_LONG).show();
//                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
