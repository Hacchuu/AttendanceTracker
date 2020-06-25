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


public class MainActivity extends AppCompatActivity implements ExampleAdapter.OnItemListener, subjectDialog.onInput {
    private ArrayList<ExampleItem> exampleItems;
    private ArrayList<AttendanceDetails> attendanceDetails;
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
    private  RecyclerView.LayoutManager layoutManager;
    ArrayList<AttendanceDetails> det;

    public ArrayList<ExampleItem> getExampleItems() {
        return exampleItems;
    }

    public void setExampleItems(ArrayList<ExampleItem> exampleItems) {
        this.exampleItems = exampleItems;
    }

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d("Mipsa name", "Here 9");
//        


        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();


        if(user == null){
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        else {
            uid = user.getUid();
            ref = database.getReference().getRoot();
        }
        name_target_callDB();
        subjectCallDb();
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

    private  void name_target_callDB(){
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        Log.v("temp", "Harsh setting  DB listener");

        checkRef.child("Name").addValueEventListener(new ValueEventListener() {
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

        checkRef.child("Target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "inside ondatachange " + dataSnapshot);
                dataName = (String)dataSnapshot.getValue();
                textView2.setText(dataName);
                minimumAttendance = dataName;
//
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp", "Harsh read failed" + databaseError);
            }
        });
    }

    private void subjectCallDb(){
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        checkRef.child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "raj inside ondatachange " + dataSnapshot);
                exampleItems.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String sName = postSnapshot.child("subjectName").getValue(String.class);
                    int ab = postSnapshot.child("absent").getValue(Integer.class);
                    int att = postSnapshot.child("attend").getValue(Integer.class);
                    int bun = postSnapshot.child("bunk").getValue(Integer.class);
                    int pre = postSnapshot.child("present").getValue(Integer.class);
                    int tot = postSnapshot.child("total").getValue(Integer.class);
                    float percent = postSnapshot.child("percentage").getValue(Float.class);
                    det = new ArrayList<AttendanceDetails>();
                    for(DataSnapshot snapshot: postSnapshot.child("attendanceDetails").getChildren()){
                        det.add(snapshot.getValue(AttendanceDetails.class));

                    };
                    ExampleItem data = new ExampleItem(sName,pre,ab,tot,percent,bun,att,det);
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
    }

    public void createExampleList(){
        exampleItems = new ArrayList<>();
    }
//
    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ExampleAdapter(exampleItems, this, this);
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
        ExampleItem subjectItem = new ExampleItem(inputSubject, 0, 0, 0, 0, 0, 0,null);
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


    @Override
    public void OnItemClick(int position) {
//        exampleItems.get(position);
        Intent intent = new Intent(getApplicationContext(),SubjectDetails.class );
        intent.putExtra("Selected Subject Item",exampleItems.get(position));
        startActivity(intent);
    }
}


