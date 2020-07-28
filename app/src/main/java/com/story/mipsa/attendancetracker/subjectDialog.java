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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatDialogFragment;

import static android.content.Context.VIBRATOR_SERVICE;

public class subjectDialog extends AppCompatDialogFragment {

    public interface onInput {
        void sendInput(String input);
    }

    public subjectDialog.onInput onInput;

    private EditText editText;
    private Button cancel, Add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subject_dialog, container, false);
        editText = view.findViewById(R.id.enterSubject);
        cancel = view.findViewById(R.id.cancel);
        Add = view.findViewById(R.id.add);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeItBaby();
                String subject = editText.getText().toString().trim();
                if (!subject.equals("")) {
                    onInput.sendInput(subject);
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInput = (subjectDialog.onInput) getActivity();
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
