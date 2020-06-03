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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements subjectDialog.onInput{
    private ArrayList<ExampleItem> exampleItems;
//    public static MainEmptyActivity mainEmptyActivity;
    TextView textView;
    TextView textView2;
    public static String minimumAttendance;
    AttendanceTarget target;
    private Button insertButton;
    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private FirebaseUser user;
    public String sub;
    String dataName;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;

    String Name;
    private  RecyclerView.LayoutManager layoutManager;


    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        setContentView(R.layout.activity_main);

        Log.d("Mipsa name", "Here 9");
//        

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();

        if(user == null){
            startActivity(new Intent(getApplicationContext(),Login.class));
        }

        uid = user.getUid();
        ref = database.getReference().getRoot();

        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        Log.v("temp", "Harsh setting  DB listener");

        checkRef.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "harsh inside ondatachange " + dataSnapshot);
                dataName = (String)dataSnapshot.getValue();
                textView.setText(dataName);
//                Log.v("check",dataName);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp", "Harsh read failed" + databaseError);
            }
        });

        checkRef.child("Target").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "inside ondatachange " + dataSnapshot);
                dataName = (String)dataSnapshot.getValue();
                textView2.setText(dataName);
                minimumAttendance = dataName;
//                Log.v("check",dataName);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp", "Harsh read failed" + databaseError);
            }
        });

        checkRef.child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "raj inside ondatachange " + dataSnapshot);
                exampleItems.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ExampleItem data = postSnapshot.getValue(ExampleItem.class);
                    exampleItems.add(data);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp",   "raj read failed" + databaseError);
            }
        });



        createExampleList();
        buildRecyclerView();
        textView = findViewById(R.id.Name);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FirstPage.class);
                startActivity(intent);
            }
        });

        textView2 = findViewById(R.id.TargetFill);
        textView2.setText(minimumAttendance);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AttendanceTarget.class);
                startActivity(intent);
            }
        });

        insertButton = findViewById(R.id.addSubject);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subjectDialog dialog = new subjectDialog();
                dialog.show(getSupportFragmentManager(),"subjectDialog");

            }
        });
    }

    public void createExampleList(){
        exampleItems = new ArrayList<>();
    }
//
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

    public void insertItem(String inputSubject,int position) {
        ExampleItem subjectItem = new ExampleItem(inputSubject, 0, 0, 0, 0, 0, 0);
        Log.v("raj insertItem si", "" + subjectItem);
        exampleItems.add(position, subjectItem);


    //Adding data to firebase
        user = firebaseAuth.getCurrentUser();
        DatabaseReference userRef = ref.child("Users");
        Log.v("raj subject", inputSubject + "----->" + subjectItem);
        userRef.child(user.getUid()).child("Subjects").child(inputSubject).setValue(subjectItem);

        adapter.notifyDataSetChanged();
//        recyclerView.scheduleLayoutAnimation();
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


