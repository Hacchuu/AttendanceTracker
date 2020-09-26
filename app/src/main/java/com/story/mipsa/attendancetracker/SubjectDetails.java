package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SubjectDetails extends AppCompatActivity implements AttendanceItemAdapter.OnTimelimeListener, EditSubjectDetails.OnInput, ExtraClassEdit.OnInput1{
    private TextView subjectName;
    private TextView total;
    private TextView present;
    private TextView absent;
    private TextView attendance;
    private TextView outcome;
    private SubjectItem currentSubjectItem;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SubjectAttendanceDetails> subjectAttendanceDetailsList;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseUser user;
    private int position;
    MainActivity mainActivity;
    AttendanceTarget attendanceTarget;
    private String index;

    public ArrayList<SubjectAttendanceDetails> getSubjectAttendanceDetailsList() {
        return subjectAttendanceDetailsList;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public SubjectItem getCurrentSubjectItem() {
        return currentSubjectItem;
    }

    //Functions to perform when back is pressed form subject details page
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);
        ActionBar actionBar = getSupportActionBar();
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#151515"));
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);

        ImageView logo = view.findViewById(R.id.logo);
        logo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_black_24dp));
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
            index = getIntent().getStringExtra("index");

        }
        setIndex(index);
        subjectName = findViewById(R.id.nameSubjectDets);
        attendance = findViewById(R.id.percent);
        total = findViewById(R.id.totalFill);
        present = findViewById(R.id.presentFill);
        absent = findViewById(R.id.absentFill);
        outcome = findViewById(R.id.outcome);
        setViews();
        details_DB();
        createAttendanceList();
        buildRecyclerView();
    }

    private  void details_DB(){
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        //Retrieve the attendance details and store in its appropriate list
        checkRef.child("Subjects").child(index).child("subjectAttendanceDetails").orderByChild("dateOfEntry").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectAttendanceDetailsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SubjectAttendanceDetails data = postSnapshot.getValue(SubjectAttendanceDetails.class);
                    subjectAttendanceDetailsList.add(data);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }
                Collections.reverse(subjectAttendanceDetailsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAttendanceList() {
        subjectAttendanceDetailsList = new ArrayList<>();
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.detailsRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new AttendanceItemAdapter(subjectAttendanceDetailsList, this,this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    //When the timeline is clicked for updation of data
    @Override
    public void onTimelineClick(int position) {
        this.position = position;

        if(subjectAttendanceDetailsList.get(position).isExtraClass()){
            ExtraClassEdit eDialog = new ExtraClassEdit();
            eDialog.show(getSupportFragmentManager(), "ExtraClassEdit");
        }
        else{
            EditSubjectDetails dialog = new EditSubjectDetails();
            dialog.show(getSupportFragmentManager(), "EditSubjectDetails");
        }
    }

    @Override
    public void sendDetailsInput(String status, long date) {
        if(date == 0){
            date = subjectAttendanceDetailsList.get(position).getDateOfEntry();
        }
        updateDetails(status, date, position);
    }

    private void updateDetails(String status, long date, int position) {
        for(int i=0;i<subjectAttendanceDetailsList.size();i++){
            boolean checkExtra = subjectAttendanceDetailsList.get(position).isExtraClass();
            if(checkExtra)
                break;
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
            String currentDate2 = sdf.format(subjectAttendanceDetailsList.get(i).getDateOfEntry());
            String checkDate = sdf.format(date);
            boolean checkExtraItem = subjectAttendanceDetailsList.get(i).isExtraClass();
            if (currentDate2.equalsIgnoreCase(checkDate) && i != position ) {
                if(!checkExtraItem){
                    Toast.makeText(this, "You have already entered the attendance for " + currentDate2, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        boolean extraClass = subjectAttendanceDetailsList.get(position).isExtraClass();

        SubjectAttendanceDetails updateItem = new SubjectAttendanceDetails(status, date,extraClass, position);
        if (status.equalsIgnoreCase("present") && !subjectAttendanceDetailsList.get(position).getStatus().equalsIgnoreCase(status)) {
            subjectAttendanceDetailsList.get(position).setStatus(status);
            currentSubjectItem.setPresent(currentSubjectItem.getPresent() + 1);
            currentSubjectItem.setAbsent(currentSubjectItem.getAbsent() - 1);
            recalculate();
        } else if (status.equalsIgnoreCase("absent") && !subjectAttendanceDetailsList.get(position).getStatus().equalsIgnoreCase(status)) {
            subjectAttendanceDetailsList.get(position).setStatus(status);
            currentSubjectItem.setPresent(currentSubjectItem.getPresent() - 1);
            currentSubjectItem.setAbsent(currentSubjectItem.getAbsent() + 1);
            recalculate();
        }
        subjectAttendanceDetailsList.set(position, updateItem);
        DatabaseReference detailsRef = ref.child("Users").child(user.getUid()).child("Subjects").child(index);
        detailsRef.setValue(currentSubjectItem);
        detailsRef.child("subjectAttendanceDetails").setValue(subjectAttendanceDetailsList);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Intent intent = new Intent(getApplicationContext(),SubjectDetails.class);
        intent.putExtra("Selected Subject Item", currentSubjectItem);
        intent.putExtra("index", getIndex());
        startActivity(intent);
        finish();
    }

    //Updates the views of all the required fields after any changes.
    protected void setViews() {
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
    protected void recalculate() {
        int presentS = currentSubjectItem.getPresent();
        int totalS = currentSubjectItem.getTotal();
        int attend = 0;
        int bunk = 0;
        float avg = 0;
        if (totalS != 0) {
            avg = ((float) presentS / (float) totalS) * 100;
            currentSubjectItem.setPercentage(avg);
        }
        else{
            currentSubjectItem.setPercentage(0);
            currentSubjectItem.setAttend(0);
            currentSubjectItem.setBunk(0);
            return;
        }
        float temp = avg;
        String target = MainActivity.getMinimumAttendance();
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
