package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SubjectItemAdapter.OnItemListener, subjectDialog.onInput {
    private ArrayList<SubjectItem> subjectItems;
    TextView textView;
    TextView textView2;
    TextView countView;
    TextView dateText;
    public static String minimumAttendance;
    private Button insertButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FirebaseUser user;
    String dataName;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<AttendanceDetails> det;
    GoogleSignInClient googleSignInClient;
    int subject_count;

    public ArrayList<SubjectItem> getSubjectItems() {
        return subjectItems;
    }

    public void setSubjectItems(ArrayList<SubjectItem> subjectItems) {
        this.subjectItems = subjectItems;
    }

    public static String getMinimumAttendance() {
        return minimumAttendance;
    }

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Mipsa name", "Here 9");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();

        //current date to display in main activity
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        String currentDate = sdf.format(new Date());

        dateText = findViewById(R.id.date);
        dateText.setText(currentDate);

        if (user == null) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(new Intent(getApplicationContext(), Login.class));
        } else {
            uid = user.getUid();
            ref = database.getReference().getRoot();
        }
        if (user != null) {
            name_target_callDB();
            subjectCallDb();
            createExampleList();
            buildRecyclerView();
        }
        countView = findViewById(R.id.subjectCount);
        textView = findViewById(R.id.Name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NamePage.class);
                startActivity(intent);
            }
        });
        textView2 = findViewById(R.id.TargetFill);
        textView2.setText(minimumAttendance);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
                startActivity(intent);
            }
        });
        insertButton = findViewById(R.id.addSubject);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subjectDialog dialog = new subjectDialog();
                dialog.show(getSupportFragmentManager(), "subjectDialog");
            }
        });
    }

    //Function that retrieves the name and target attendance in the database
    private void name_target_callDB() {
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        Log.v("temp", "Harsh setting  DB listener");
        //Listener ofr Firebase DB
        checkRef.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "harsh inside ondatachange " + dataSnapshot);
                dataName = (String) dataSnapshot.getValue();
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
                dataName = (String) dataSnapshot.getValue();
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

    //Function to retrieve the subject details in the Firebase DB
    private void subjectCallDb() {
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        checkRef.child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "raj inside ondatachange " + dataSnapshot);
                subjectItems.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String sName = postSnapshot.child("subjectName").getValue(String.class);
                    int ab = postSnapshot.child("absent").getValue(Integer.class);
                    int att = postSnapshot.child("attend").getValue(Integer.class);
                    int bun = postSnapshot.child("bunk").getValue(Integer.class);
                    int pre = postSnapshot.child("present").getValue(Integer.class);
                    int tot = postSnapshot.child("total").getValue(Integer.class);
                    float percent = postSnapshot.child("percentage").getValue(Float.class);
                    det = new ArrayList<>();
                    for (DataSnapshot snapshot : postSnapshot.child("attendanceDetails").getChildren()) {
                        det.add(snapshot.getValue(AttendanceDetails.class));
                    }
                    ;
                    SubjectItem data = new SubjectItem(sName, pre, ab, tot, percent, bun, att, det);
                    subjectItems.add(data);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                    subject_count = subjectItems.size();
                    countView.setText(Integer.toString(subject_count));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp", "raj read failed" + databaseError);
            }
        });
    }

    public void createExampleList() {
        subjectItems = new ArrayList<>();
    }

    //Function to build the layout and connect the adaptor to the recycler view.
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new SubjectItemAdapter(subjectItems, this, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //Function to take input from subject dialog and inset the input in the subject list.
    @Override
    public void sendInput(String input) {
        if (subjectItems.size() == 0) {
            insertItem(input, 0);
        } else {
            insertItem(input, subjectItems.size());
        }
    }

    //Function to insert the subject details in the arraylist as well as the database.
    public void insertItem(String inputSubject, int position) {
        SubjectItem subjectItem = new SubjectItem(inputSubject, 0, 0, 0, 0, 0, 0, null);
        Log.v("raj insertItem si", "" + subjectItem);
        subjectItems.add(position, subjectItem);

        //Adding data to firebase
        user = firebaseAuth.getCurrentUser();
        DatabaseReference userRef = ref.child("Users");
        Log.v("raj subject", inputSubject + "----->" + subjectItem);
        userRef.child(user.getUid()).child("Subjects").child(inputSubject).setValue(subjectItem);
        adapter.notifyDataSetChanged();
        subject_count = subjectItems.size();
        countView.setText(Integer.toString(subject_count));

    }

    //To log out of google account or custom account.
    public void logout(View view) {
        switch (view.getId()) {
            case R.id.logout:
                signOut();
                break;
        }
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, Login.class));
    }

    public void Target() {
        Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
        startActivity(intent);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    //Intent to appropriate subject activity by clicking on item list.
    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), SubjectDetails.class);
        intent.putExtra("Selected Subject Item", subjectItems.get(position));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
    }

    //Functions to perform when back is pressed
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}


