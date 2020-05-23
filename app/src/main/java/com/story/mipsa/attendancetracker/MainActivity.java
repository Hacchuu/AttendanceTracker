package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements subjectDialog.onInput{
    private ArrayList<ExampleItem> exampleItems;
    TextView textView;
    TextView textView2;
    public static String minimumAttendance;
    AttendanceTarget target;
    private Button insertButton;
    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseUser;
    private  RecyclerView.LayoutManager layoutManager;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = user.getUid();

        databaseUser = FirebaseDatabase.getInstance().getReference();

//        DbManager dbManager = new DbManager(this);
//        int count = (int) dbManager.getCount();
//        if(count == 0){
//            Intent intent = new Intent(getApplicationContext(), FirstPage.class);
//            startActivity(intent);
//        }

        createExampleList();
        buildRecyclerView();
//        textView = (TextView)findViewById(R.id.nameFill);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),FirstPage.class);
//                startActivity(intent);
//            }
//        });

        textView2 = (TextView)findViewById(R.id.TargetFill);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AttendanceTarget.class);
                startActivity(intent);
            }
        });

        insertButton = (Button)findViewById(R.id.addSubject);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subjectDialog dialog = new subjectDialog();
                dialog.show(getSupportFragmentManager(),"subjectDialog");

            }
        });

        minimumAttendance = AttendanceTarget.minimumAttendance;
        textView2.setText(minimumAttendance);

//        Cursor cursor = dbManager.viewData();
//        while (cursor.moveToNext()){
//            textView.setText(cursor.getString(1));
//            minimumAttendance = cursor.getString(2);
//            textView2.setText(minimumAttendance);
//        }
    }


    public void createExampleList(){
        exampleItems = new ArrayList<>();
    }

    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ExampleAdapter(exampleItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void sendInput(String input) {
        if(exampleItems.size()==0){
            insertItem(input,0);
        }
        else {
            insertItem(input, exampleItems.size());
        }
    }

    public void insertItem(String inputSubject,int position){

        exampleItems.add(position,new ExampleItem(inputSubject,0,0,0,0,0,0));
        adapter.notifyDataSetChanged();
    }

    public void logout(View view){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, Login.class));
    }

    public void Target(){
        Intent intent = new Intent(getApplicationContext(),AttendanceTarget.class);
        startActivity(intent);
    }
}


