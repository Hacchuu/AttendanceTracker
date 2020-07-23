package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AttendanceTarget extends AppCompatActivity {
    TextView textView;
    Button button;
    SeekBar seekBar;
    public static String minimumAttendance;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;

    public static String getMinimumAttendance() {
        return minimumAttendance;
    }

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_target);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#556e5f"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setElevation(0);
        TextView display = view.findViewById(R.id.name);
        display.setText("Attendance Tracker");

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();
        user = firebaseAuth.getCurrentUser();

        textView = findViewById(R.id.number);
        button = findViewById(R.id.save);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int x;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                x = i;
                textView.setText(x + "%");
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
                if (!minimumAttendance.equalsIgnoreCase("%")) {
                    user = firebaseAuth.getCurrentUser();
                    DatabaseReference userRef = ref.child("Users");
                    userRef.child(user.getUid()).child("Target").setValue(minimumAttendance);
                    Toast.makeText(getApplicationContext(), "Minimum Attendance set to "+minimumAttendance, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                    finish();
                } else {
                    Toast message = Toast.makeText(getApplicationContext(), "Minimum Attendance can't be null", Toast.LENGTH_SHORT);
                    View toastView = message.getView();
                    toastView.setBackgroundResource(R.drawable.toast_color);
                    TextView v = message.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);
                    message.show();
                }
            }
        });
    }

}
