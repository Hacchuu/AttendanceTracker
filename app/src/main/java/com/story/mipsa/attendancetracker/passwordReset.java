package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class passwordReset extends AppCompatActivity {

    EditText email;
    Button button;
    FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#556e5f"));
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setElevation(0);
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);
        display.setText("Attendance Tracker");

        email =  findViewById(R.id.ResetEmail);
        button =  findViewById(R.id.Confirmation);
        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rEmail = email.getText().toString().trim();
                if (rEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter the email address", Toast.LENGTH_LONG).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(rEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password Reset Email sent!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to send Email!", Toast.LENGTH_LONG).show();
                            }
                        }
                });
                }
            }
        });
    }
}
