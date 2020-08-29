package com.story.mipsa.attendancetracker;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.updateCurrentUser(null);
        user = firebaseAuth.getCurrentUser();
        if(user != null && user.isEmailVerified()){
            Intent intent = new Intent(this, MainActivity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, Login.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }
    }
}
