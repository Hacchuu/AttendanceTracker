package com.story.mipsa.attendancetracker;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

//To maintain the persistence of data in the device.
public class MyFirebaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.getInstance().setAutomaticResourceManagementEnabled(true);
    }
}
