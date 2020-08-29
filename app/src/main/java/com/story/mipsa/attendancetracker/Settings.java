package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private ArrayList<String> settingNames = new ArrayList<>();
    private ArrayList<Integer> settingImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#151515"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);
        ImageView logo = view.findViewById(R.id.logo);
        logo.setVisibility(View.INVISIBLE);
        display.setText("Settings");

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.Settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.Home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(),"You selected Home",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Reminder:
                        Intent intent1 = new Intent(getApplicationContext(), Reminder.class);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent1);
                        finish();
                        Toast.makeText(getApplicationContext(),"You selected Reminder",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Settings:
                        Toast.makeText(getApplicationContext(),"You selected Help",Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        initSettingsList();
        initRecyclerView();
    }

    @Override
    public void onBackPressed() {
        bottomNavigationView.setSelectedItemId(R.id.Home);
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initSettingsList(){
        settingImages.add(R.drawable.ic_info_black_24dp);
        settingNames.add("Instructions");

        settingImages.add(R.drawable.ic_help_black_24dp);
        settingNames.add("Help/Support");

        settingImages.add(R.drawable.ic_exit_to_app_black_24dp);
        settingNames.add("Logout");
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerSettings);
        settingsAdapter adapter = new settingsAdapter(settingNames,settingImages,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
