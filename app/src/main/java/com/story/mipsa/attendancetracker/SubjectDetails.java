package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectDetails extends AppCompatActivity implements AttendanceItemAdapter.OnTimelimeListener, editDetails.onInput{
    TextView subjectName;
    TextView total;
    TextView present;
    TextView absent;
    TextView attendance;
    TextView outcome;
    ExampleItem currentSubjectItem;
    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private  RecyclerView.LayoutManager layoutManager;
    private ArrayList<AttendanceDetails> attendanceDetailsList;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser user;
    private int position;
    MainActivity mainActivity;
    AttendanceTarget attendanceTarget;
//    ExampleItem item;

    public ArrayList<AttendanceDetails> getAttendanceDetailsList() {
        return attendanceDetailsList;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        mainActivity = new MainActivity();
        attendanceTarget = new AttendanceTarget();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        ref = database.getReference().getRoot();
//        item = new ExampleItem();

//        attendanceDetailsList = mainActivity.getDet();


        if(getIntent().hasExtra("Selected Subject Item")){
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

        checkRef.child("Subjects").child(currentSubjectItem.getSubjectName()).child("attendanceDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("temp", "raj inside ondatachange " + dataSnapshot);
                attendanceDetailsList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    AttendanceDetails data = postSnapshot.getValue(AttendanceDetails.class);
//                    item.setAttendanceDetails(data);
                    attendanceDetailsList.add(data);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("temp",   "raj read failed" + databaseError);
            }
        });
        createAttendanceList();
        buildRecyclerView();

    }

    private void createAttendanceList() {
        attendanceDetailsList = new ArrayList<>();
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.detailsRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new AttendanceItemAdapter(attendanceDetailsList,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTimelineClick(int position) {
        this.position = position;
        editDetails dialog = new editDetails();
        dialog.show(getSupportFragmentManager(),"editDetails");
    }

    @Override
    public void sendDetailsInput(String status, String date) {
        Log.d("harsh edited info", status+"----"+date+"----"+position);
        updateDetails(status,date,position);
    }

    public void addClass(){
        editDetails dialog = new editDetails();
        dialog.show(getSupportFragmentManager(),"editDetails");
    }

     private void updateDetails(String status, String date, int position) {
        Log.d("inside update", status+date+" - "+position);
        AttendanceDetails updateItem = new AttendanceDetails(status,date);

         if(status.equalsIgnoreCase("present") && !attendanceDetailsList.get(position).getStatus().equalsIgnoreCase(status)){
             attendanceDetailsList.get(position).setStatus(status);
             currentSubjectItem.setPresent(currentSubjectItem.getPresent()+1);
             currentSubjectItem.setAbsent(currentSubjectItem.getAbsent()-1);
             Recalculate();

         }
         else if(status.equalsIgnoreCase("absent") && !attendanceDetailsList.get(position).getStatus().equalsIgnoreCase(status)){
             attendanceDetailsList.get(position).setStatus(status);
             currentSubjectItem.setPresent(currentSubjectItem.getPresent()-1);
             currentSubjectItem.setAbsent(currentSubjectItem.getAbsent()+1);
             Recalculate();

         }

        attendanceDetailsList.set(position,updateItem);
        Log.d("updated list", ""+updateItem+"========"+attendanceDetailsList);
        buildRecyclerView();
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

        DatabaseReference detailsRef = ref.child("Users").child(user.getUid()).child("Subjects").child(currentSubjectItem.getSubjectName());
        detailsRef.setValue(currentSubjectItem);

        detailsRef.child("attendanceDetails").setValue(attendanceDetailsList);
        setViews();


    }

    private void setViews(){
        subjectName.setText(currentSubjectItem.getSubjectName());
        attendance.setText(String.format(java.util.Locale.US,"%.1f",currentSubjectItem.getPercentage())+"%");
        total.setText(String.valueOf(currentSubjectItem.getTotal()));
        present.setText(String.valueOf(currentSubjectItem.getPresent()));
        absent.setText(String.valueOf(currentSubjectItem.getAbsent()));
        if(currentSubjectItem.getAttend()!= 0){
            if(currentSubjectItem.getAttend() == 1){
                outcome.setText("You have to attend the next class");
            }
            else {
                outcome.setText("You have to attend the next " + currentSubjectItem.getAttend() + " classes");
            }
        }
        else if(currentSubjectItem.getBunk() != 0){
            if(currentSubjectItem.getBunk() == 1){
                outcome.setText("You can bunk the next class");
            }
            else {
                outcome.setText("You can bunk the next " + currentSubjectItem.getBunk() + " classes");
            }
        }
    }

    private void Recalculate() {
        int absentS = currentSubjectItem.getAbsent();
        int presentS = currentSubjectItem.getPresent();
        int total = currentSubjectItem.getTotal();
        int totalS = total;
        int attend = 0;
        int bunk = 0;
        float avg = 0;
        if(totalS != 0){
            avg = ((float)presentS/(float)totalS)*100;
            currentSubjectItem.setPercentage(avg);
        }
        float temp = avg;
        String target = mainActivity.minimumAttendance;
        String target2 = "";
        int min;
        for(int i=0;i<3;i++){
            if(target.charAt(i) == '%') {
                break;
            }
            else{
                target2 = target2 + target.charAt(i);
            }
        }
        min = Integer.parseInt(target2);

        if(temp >= min) {
            do {
                totalS += 1;
                temp = ((float) presentS / (float) totalS) * 100;
                if (temp < min && bunk == 0) {
                    attend++;
                } else if (temp >= min && attend == 0)
                    bunk++;
            } while (temp > min);
        }
        else{
            int presentTemp = presentS;
            do {
                totalS += 1;
                presentTemp+=1;
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
