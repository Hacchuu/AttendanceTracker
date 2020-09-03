package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private BottomNavigationView bottomNavigationView;
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
        display.setText("Settings");

        ImageView logo = view.findViewById(R.id.logo);
        logo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_black_24dp));
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
        settingNames.add("Information");

        settingImages.add(R.drawable.ic_help_black_24dp);
        settingNames.add("Help/Support");

        settingImages.add(R.drawable.ic_exit_to_app_black_24dp);
        settingNames.add("Logout");
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerSettings);
        SettingsAdapter adapter = new SettingsAdapter(settingNames,settingImages,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
