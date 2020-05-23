package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class AttendanceTarget extends AppCompatActivity {
    TextView textView;
    Button button;
    SeekBar seekBar;
    public static String minimumAttendance;
    FirstPage firstPage;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_target);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();

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

                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                userRef.child(user.getUid()).child("Target").setValue(minimumAttendance);

                Toast message = Toast.makeText(getApplicationContext(),"Minimum Attendance set to "+minimumAttendance+"%",Toast.LENGTH_SHORT);
                View toastView = message.getView();
                toastView.setBackgroundResource(R.drawable.toast_color);
                TextView v = (TextView) message.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.BLACK);
                message.show();


//


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
