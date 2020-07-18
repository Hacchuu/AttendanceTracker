package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirstPage extends AppCompatActivity {
    public static String name;
    EditText editText;
    Button button;
    AttendanceTarget attendanceTarget;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;
    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();
        user = firebaseAuth.getCurrentUser();

        //Check whether name already exists in database or not
        //If exists, go to next activity


        editText = findViewById(R.id.Name);
        button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editText.getText().toString().trim();
                Log.v("temp", "raj inside onclick");
                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                userRef.child(user.getUid()).child("Name").setValue(name);


                Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
                startActivity(intent);
            }
        });
    }
}
