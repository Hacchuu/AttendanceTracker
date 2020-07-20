package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectDetails extends AppCompatActivity implements AttendanceItemAdapter.OnTimelimeListener, editSubjectDetails.onInput {
    TextView subjectName;
    TextView total;
    TextView present;
    TextView absent;
    TextView attendance;
    TextView outcome;
    SubjectItem currentSubjectItem;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SubjectAttendanceDetails> subjectAttendanceDetailsList;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser user;
    private int position;
    MainActivity mainActivity;
    AttendanceTarget attendanceTarget;

    public ArrayList<SubjectAttendanceDetails> getSubjectAttendanceDetailsList() {
        return subjectAttendanceDetailsList;
    }

    //Functions to perform when back is pressed form subject details page
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        intent.putExtra("splash_disable", 1);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#556e5f"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);
        display.setText("Subject Details");


        mainActivity = new MainActivity();
        attendanceTarget = new AttendanceTarget();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        ref = database.getReference().getRoot();

        //retrieve the details of the subject that is clicked in main Activity from adaptor class
        if (getIntent().hasExtra("Selected Subject Item")) {
            currentSubjectItem = getIntent().getParcelableExtra("Selected Subject Item");
        }

        subjectName = findViewById(R.id.nameSubject);
        attendance = findViewById(R.id.percent);
        total = findViewById(R.id.totalFill);
        present = findViewById(R.id.presentFill);
        absent = findViewById(R.id.absentFill);
        outcome = findViewById(R.id.outcome);
        setViews();

        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        //Retriece the attendance details and store in its appropriate list
        checkRef.child("Subjects").child(currentSubjectItem.getSubjectName()).child("subjectAttendanceDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "raj inside ondatachange " + dataSnapshot);
                subjectAttendanceDetailsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SubjectAttendanceDetails data = postSnapshot.getValue(SubjectAttendanceDetails.class);
                    subjectAttendanceDetailsList.add(data);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp", "raj read failed" + databaseError);
            }
        });
        createAttendanceList();
        buildRecyclerView();

    }

    private void createAttendanceList() {
        subjectAttendanceDetailsList = new ArrayList<>();
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.detailsRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new AttendanceItemAdapter(subjectAttendanceDetailsList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //When the timeline is clicked for updation of data
    @Override
    public void onTimelineClick(int position) {
        this.position = position;
        editSubjectDetails dialog = new editSubjectDetails();
        dialog.show(getSupportFragmentManager(), "editSubjectDetails");
    }

    @Override
    public void sendDetailsInput(String status, String date) {
        Log.d("harsh edited info", status + "----" + date + "----" + position);
        updateDetails(status, date, position);
    }

    public void addClass() {
        editSubjectDetails dialog = new editSubjectDetails();
        dialog.show(getSupportFragmentManager(), "editSubjectDetails");
    }

    private void updateDetails(String status, String date, int position) {
        Log.d("inside update", status + date + " - " + position);
        SubjectAttendanceDetails updateItem = new SubjectAttendanceDetails(status, date);

        if (status.equalsIgnoreCase("present") && !subjectAttendanceDetailsList.get(position).getStatus().equalsIgnoreCase(status)) {
            subjectAttendanceDetailsList.get(position).setStatus(status);
            currentSubjectItem.setPresent(currentSubjectItem.getPresent() + 1);
            currentSubjectItem.setAbsent(currentSubjectItem.getAbsent() - 1);
            Recalculate();

        } else if (status.equalsIgnoreCase("absent") && !subjectAttendanceDetailsList.get(position).getStatus().equalsIgnoreCase(status)) {
            subjectAttendanceDetailsList.get(position).setStatus(status);
            currentSubjectItem.setPresent(currentSubjectItem.getPresent() - 1);
            currentSubjectItem.setAbsent(currentSubjectItem.getAbsent() + 1);
            Recalculate();

        }
        subjectAttendanceDetailsList.set(position, updateItem);
        Log.d("updated list", "" + updateItem + "========" + subjectAttendanceDetailsList);
        buildRecyclerView();
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        DatabaseReference detailsRef = ref.child("Users").child(user.getUid()).child("Subjects").child(currentSubjectItem.getSubjectName());
        detailsRef.setValue(currentSubjectItem);
        detailsRef.child("subjectAttendanceDetails").setValue(subjectAttendanceDetailsList);
        setViews();
    }

    //Updates the views of all the required fields after any changes.
    private void setViews() {
        subjectName.setText(currentSubjectItem.getSubjectName());
        attendance.setText(String.format(java.util.Locale.US, "%.1f", currentSubjectItem.getPercentage()) + "%");
        total.setText(String.valueOf(currentSubjectItem.getTotal()));
        present.setText(String.valueOf(currentSubjectItem.getPresent()));
        absent.setText(String.valueOf(currentSubjectItem.getAbsent()));
        if (currentSubjectItem.getAttend() != 0) {
            if (currentSubjectItem.getAttend() == 1) {
                outcome.setText("You have to attend the next class");
            } else {
                outcome.setText("You have to attend the next " + currentSubjectItem.getAttend() + " classes");
            }
        } else if (currentSubjectItem.getBunk() != 0) {
            if (currentSubjectItem.getBunk() == 1) {
                outcome.setText("You can bunk the next class");
            } else {
                outcome.setText("You can bunk the next " + currentSubjectItem.getBunk() + " classes");
            }
        }
    }

    //Recalculates the algorithm if there are any changes during updation of timeline.
    private void Recalculate() {
        int absentS = currentSubjectItem.getAbsent();
        int presentS = currentSubjectItem.getPresent();
        int total = currentSubjectItem.getTotal();
        int totalS = total;
        int attend = 0;
        int bunk = 0;
        float avg = 0;
        if (totalS != 0) {
            avg = ((float) presentS / (float) totalS) * 100;
            currentSubjectItem.setPercentage(avg);
        }
        float temp = avg;
        String target = mainActivity.minimumAttendance;
        String target2 = "";
        int min;
        for (int i = 0; i < 3; i++) {
            if (target.charAt(i) == '%') {
                break;
            } else {
                target2 = target2 + target.charAt(i);
            }
        }
        min = Integer.parseInt(target2);

        if (temp >= min) {
            do {
                totalS += 1;
                temp = ((float) presentS / (float) totalS) * 100;
                if (temp < min && bunk == 0) {
                    attend++;
                } else if (temp >= min && attend == 0)
                    bunk++;
            } while (temp > min);
        } else {
            int presentTemp = presentS;
            do {
                totalS += 1;
                presentTemp += 1;
                temp = ((float) presentTemp / (float) totalS) * 100;
                if (temp <= min && bunk == 0) {
                    attend++;
                } else if (temp > min && attend == 0)
                    bunk++;
            } while (temp <= min);
        }
        currentSubjectItem.setAttend(attend);
        currentSubjectItem.setBunk(bunk);
    }

}
