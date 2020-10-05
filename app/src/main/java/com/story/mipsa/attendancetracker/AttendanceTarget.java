package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    private TextView textView;
    private static String minimumAttendance;
    private FirebaseUser user;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private int existingTarget;
    private String dataName;
    private SeekBar seekBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_target);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#556e5f"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setElevation(0);
        TextView display = view.findViewById(R.id.name);
        display.setText("Student Pocket");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();
        user = firebaseAuth.getCurrentUser();
        String initTarget = getIntent().getStringExtra("initialTarget");


        StringBuilder target2 = new StringBuilder();
        for (int i = 0; i < initTarget.length(); i++) {
            if (initTarget.charAt(i) == '%') {
                break;
            } else {
                target2.append(initTarget.charAt(i));
            }
        }
        existingTarget = Integer.parseInt(target2.toString());

        textView = findViewById(R.id.number);
        Button button = findViewById(R.id.save);
        seekBar = findViewById(R.id.seekBar);

        user = firebaseAuth.getCurrentUser();
        DatabaseReference userRef = ref.child("Users");


        target_callDB();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int x;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                x = i;
                textView.setText(x + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
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
                    if(minimumAttendance.equalsIgnoreCase("0%")){
                        Toast.makeText(getApplicationContext(), "Minimum attendance cannot be set to 0%", Toast.LENGTH_SHORT).show();
                    }
                    else if(minimumAttendance.equalsIgnoreCase("100%")){
                        Toast.makeText(getApplicationContext(), "Minimum attendance cannot be set to 100%", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        userRef.child(user.getUid()).child("Target").setValue(minimumAttendance);
                        Toast.makeText(getApplicationContext(), "Minimum Attendance set to "+minimumAttendance, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Minimum Attendance can't be set to this value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void target_callDB() {
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        //Listener ofr Firebase DB
        checkRef.child("Target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot != null) {
                    dataName = (String) dataSnapshot.getValue();
                    textView.setText(dataName);
                    StringBuilder target3 = new StringBuilder();
                    for (int i = 0; i < dataName.length(); i++) {
                        if (dataName.charAt(i) == '%') {
                            break;
                        } else {
                            target3.append(dataName.charAt(i));
                        }
                    }
                    seekBar.setProgress(Integer.parseInt(target3.toString()));
                }
                else{
                    checkRef.child("Target").setValue(existingTarget+"%");
                    seekBar.setProgress(existingTarget);
                    textView.setText(existingTarget + "%");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
