package com.story.mipsa.attendancetracker;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
//    implements OnSuccessListener<AppUpdateInfo>

//    private static final int REQUEST_CODE = 1234;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
//    AppUpdateManager appUpdateManager;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == REQUEST_CODE){
//            if(resultCode == RESULT_OK){
//                Log.i("Splash Activity", "onActivityResult: Update flow completed" + resultCode);
//            }
//            else{
//                Log.e("Splash Activity", "onActivityResult: Updare flow failed" + resultCode);
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//       appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(this);


        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.updateCurrentUser(null);
        user = firebaseAuth.getCurrentUser();
        if(user != null && user.isEmailVerified()){
            Intent intent = new Intent(this, MainActivity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, Login.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }
    }

//    @Override
//    public void onSuccess(AppUpdateInfo appUpdateInfo) {
//        if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
//            startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
//        }
//        else if(appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
//            Log.d("Splash Activity", "onSuccess: Downloaded");
//        }
//        else if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
//            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
//            }
//        }
//    }

//
//    private void startUpdate(AppUpdateInfo appUpdateInfo, int immediate) {
//        final Activity activity = this;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
//                            immediate,
//                            activity,
//                            REQUEST_CODE);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}
