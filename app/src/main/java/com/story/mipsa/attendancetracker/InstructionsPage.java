package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InstructionsPage extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_page);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#556e5f"));
        ActionBar actionBar = getSupportActionBar();
//        getSupportActionBar().setElevation(0);
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);
        display.setText("App Information");

    }
}
