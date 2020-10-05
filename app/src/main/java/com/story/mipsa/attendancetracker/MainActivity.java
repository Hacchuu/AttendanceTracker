package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;


public class MainActivity extends AppCompatActivity implements SubjectItemAdapter.OnItemListener, SubjectDialog.onInput, DatePickerListener, ExtraClassDialog.OnInput2 {
    private ArrayList<SubjectItem> subjectItems;
    private TextView textView2;
    private TextView countView;
    private static String minimumAttendance;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FirebaseUser user;
    private String dataName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SubjectAttendanceDetails> det;
    private int subjectCount;
    private long selectedDate;

    public long getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(long selectedDate) {
        this.selectedDate = selectedDate;
    }

    public static String getMinimumAttendance() {
        return minimumAttendance;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#151515"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);
        ImageView logo = view.findViewById(R.id.logo);
        logo.setVisibility(View.INVISIBLE);
        display.setText("Student Pocket");


        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 0);

        final short daysInPast = 30;
        HorizontalPicker picker = findViewById(R.id.datePicker);
        picker
                .setListener(this)
                .setDateSelectedColor(getColor(R.color.background))
                .setDays(daysInPast+4)
                .setTodayButtonTextColor(Color.BLACK)
                .showTodayButton(true   )
                .setOffset(daysInPast)
                .init();

        picker.setBackground(getDrawable(R.drawable.rounded_corner));
        picker.setDate(new DateTime());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.Home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.Home:
                        return true;

                    case R.id.Settings:
                        Intent intent1 = new Intent(getApplicationContext(), Settings.class);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent1);
                        finish();
                        return true;
                }
                return false;
            }
        });

        String uid = user.getUid();
        ref = database.getReference().getRoot();
        ref.keepSynced(true);

        name_target_callDB();

        subjectCallDb();
        createExampleList();
        buildRecyclerView();
        countView = findViewById(R.id.subjectCount);
        textView2 = findViewById(R.id.TargetFill);
        textView2.setText(minimumAttendance);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
                intent.putExtra("initialTarget", minimumAttendance);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });

        Button insertButton = findViewById(R.id.addSubject);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("Subject List", subjectItems);
                SubjectDialog dialog = new SubjectDialog();
                dialog.show(getSupportFragmentManager(), "SubjectDialog");
            }
        });
    }

    //Function that retrieves the name and target attendance in the database
    private void name_target_callDB() {
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        //Listener ofr Firebase DB
        checkRef.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataName = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        checkRef.child("Target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataName = (String) dataSnapshot.getValue();
                    textView2.setText(dataName);
                    minimumAttendance = dataName;
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
                    intent.putExtra("initialTarget", "75");
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function to retrieve the subject details in the Firebase DB
    private void subjectCallDb() {
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        checkRef.child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectItems.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String sName = postSnapshot.child("subjectName").getValue(String.class);
                    int ab = postSnapshot.child("absent").getValue(Integer.class);
                    int att = postSnapshot.child("attend").getValue(Integer.class);
                    int bun = postSnapshot.child("bunk").getValue(Integer.class);
                    int pre = postSnapshot.child("present").getValue(Integer.class);
                    int tot = postSnapshot.child("total").getValue(Integer.class);
                    float percent = postSnapshot.child("percentage").getValue(Float.class);
                    int id = postSnapshot.child("id").getValue(Integer.class);
                    det = new ArrayList<>();
                    for (DataSnapshot snapshot : postSnapshot.child("subjectAttendanceDetails").getChildren()) {
                        det.add(snapshot.getValue(SubjectAttendanceDetails.class));
                    }
                    SubjectItem data = new SubjectItem(sName, pre, ab, tot, percent, bun, att, id, det);
                    subjectItems.add(data);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                    subjectCount = subjectItems.size();
                    countView.setText(Integer.toString(subjectCount));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createExampleList() {
        subjectItems = new ArrayList<>();
    }

    //Function to build the layout and connect the adaptor to the recycler view.
    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new SubjectItemAdapter(subjectItems, this, this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setHasStableIds(true);
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
        int repeat = 0;
        for(int i=0 ;i<subjectItems.size();i++){
            if(subjectItems.get(i).getSubjectName().equalsIgnoreCase(inputSubject)){
                repeat = 1;
            }
        }
        if(repeat == 1){
            Toast.makeText(getApplicationContext(), "This subject already exists", Toast.LENGTH_SHORT).show();
        }
        else{
            SubjectItem subjectItem = new SubjectItem(inputSubject, 0, 0, 0, 0, 0, 0, position, null);
            subjectItems.add(position, subjectItem);
            //Adding data to firebase
            user = firebaseAuth.getCurrentUser();
            DatabaseReference userRef = ref.child("Users");
            userRef.child(user.getUid()).child("Subjects").setValue(subjectItems);
            adapter.notifyItemInserted(position);
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                @Override protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(position);
            layoutManager.startSmoothScroll(smoothScroller);

            subjectCount = subjectItems.size();
            countView.setText(Integer.toString(subjectCount));
        }
    }

    public void Target() {
        Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    //Intent to appropriate subject activity by clicking on item list.
    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), SubjectDetails.class);
        intent.putExtra("Selected Subject Item", subjectItems.get(position));
        intent.putExtra("index", String.valueOf(position));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    //Functions to perform when back is pressed
    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
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
        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }



    @Override
    public void onDateSelected(DateTime dateSelected) {
        setSelectedDate(dateSelected.getMillis());
    }

    @Override
    public void sendExtraInput(String status, int position) {
        insertExtraClass(status, position);
    }

    private void insertExtraClass(String status, int position) {
        SubjectItem current = subjectItems.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        String currentDate = sdf.format(getSelectedDate());
        ArrayList<SubjectAttendanceDetails> attendanceDetails = current.getSubjectAttendanceDetails();
        String ind = String.valueOf(subjectItems.indexOf(current));
        if (status.equalsIgnoreCase("Present")) {
            current.setPresent(current.getPresent() + 1);
            current.setTotal(current.getTotal() + 1);

            current.setSubjectAttendanceDetails(new SubjectAttendanceDetails(status, getSelectedDate(), true, attendanceDetails.size()+1));
            recalculate(current);
            user = firebaseAuth.getCurrentUser();
            DatabaseReference userRef = ref.child("Users");
            userRef.child(user.getUid()).child("Subjects").child(ind).setValue(current);
            adapter.notifyDataSetChanged();

        } else if (status.equalsIgnoreCase("Absent")) {
            current.setAbsent(current.getAbsent() + 1);
            current.setTotal(current.getTotal() + 1);
            current.setSubjectAttendanceDetails(new SubjectAttendanceDetails(status, getSelectedDate(), true, attendanceDetails.size()+1));
            recalculate(current);
            user = firebaseAuth.getCurrentUser();
            DatabaseReference userRef = ref.child("Users");
            userRef.child(user.getUid()).child("Subjects").child(ind).setValue(current);
            adapter.notifyDataSetChanged();
        }
    }

    private void recalculate(SubjectItem current) {
        int presentS = current.getPresent();
        int total = current.getTotal();
        int attend = 0;
        int bunk = 0;
        float avg;
        if (total != 0) {
            avg = ((float) presentS / (float) total) * 100;
            current.setPercentage(avg);
        }
        else{
            current.setPercentage(0);
            current.setAttend(0);
            current.setBunk(0);
            return;
        }
        float temp = avg;
        String target = getMinimumAttendance();
        StringBuilder target2 = new StringBuilder();
        int min;
        for (int i = 0; i < 3; i++) {
            if (target.charAt(i) == '%') {
                break;
            } else {
                target2.append(target.charAt(i));
            }
        }
        min = Integer.parseInt(target2.toString());
        if (temp >= min) {
            do {
                total += 1;
                temp = ((float) presentS / (float) total) * 100;
                if (temp < min && bunk == 0) {
                    attend++;
                } else if (temp >= min && attend == 0)
                    bunk++;
            } while (temp > min);
        } else {
            int presentTemp = presentS;
            do {
                total += 1;
                presentTemp += 1;
                temp = ((float) presentTemp / (float) total) * 100;
                if (temp <= min && bunk == 0) {
                    attend++;
                } else if (temp > min && attend == 0)
                    bunk++;
            } while (temp <= min);
        }
        current.setAttend(attend);
        current.setBunk(bunk);
    }
}


