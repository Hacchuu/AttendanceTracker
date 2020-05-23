package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstPage extends AppCompatActivity {
    public static String name;
    EditText editText;
    Button button;
    AttendanceTarget attendanceTarget;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();


//        Cursor cursor = dbManager.viewData();
////        StringBuilder stringBuilder = new StringBuilder();
//        while (cursor.moveToNext()){
//            if(cursor.getString(1) != null){
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        }

        //Check whether name already exists in database or not
        //If exists, go to next activity


        editText =(EditText)findViewById(R.id.Name);
        button = (Button)findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new DbManager(getApplicationContext());

                name = editText.getText().toString().trim();

                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                userRef.child(user.getUid()).child("Name").setValue(name);


                Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
                startActivity(intent);
//                DbManager dbManager = new DbManager(getApplicationContext());
//                int count = (int) dbManager.getCount();
//                if(count == 1){
//                    String res2 = dbManager.updateRecordName(name,  1); //Problem that minimumAttendance data vanishes while updating record
//                    if (res2 == "Success")
//                        Toast.makeText(getApplicationContext(), res2, Toast.LENGTH_LONG).show();
//                    else
//                        Toast.makeText(getApplicationContext(), res2, Toast.LENGTH_LONG).show();
//
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                }
//                else{

//                }
            }
        });
    }
}
