package com.story.mipsa.attendancetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatDialogFragment;

import static android.content.Context.VIBRATOR_SERVICE;

public class ExtraClassDialog extends AppCompatDialogFragment {

    public interface onInput2 {
        void sendExtraInput(String status, int position);
    }

    public ExtraClassDialog.onInput2 onInput2;

    TextView subject;
    private Button cancelExtra, addExtra;
    private RadioButton radioPresent, radioAbsent;
//    String status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.extra_class_dialog, container, false);
//        editText = view.findViewById(R.id.enterSubject);
        subject = view.findViewById(R.id.extraSubjectName);
        cancelExtra = view.findViewById(R.id.cancelExtra);
        addExtra = view.findViewById(R.id.addExtra);
        radioPresent = view.findViewById(R.id.radioExtraPresent);
        radioAbsent = view.findViewById(R.id.radioExtraAbsent);

        Bundle bundle = getArguments();
        String subjectName = bundle.getString("SubjectName");
        final int position = bundle.getInt("position");
        subject.setText(subjectName.toUpperCase() + "-" +position);

        cancelExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeItBaby();
                getDialog().dismiss();
            }
        });
//
        addExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeItBaby();
                if (radioPresent.isChecked()) {
                    if(onInput2 != null)
                        onInput2.sendExtraInput("Present", position);
                    getDialog().dismiss();
                } else if (radioAbsent.isChecked()) {
                    if(onInput2 != null)
                        onInput2.sendExtraInput("Absent", position);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(),"Select an option",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onInput2 = (ExtraClassDialog.onInput2) getActivity();
        } catch (ClassCastException e) {

        }
    }

    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator)getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(125);
        }
    }

}
