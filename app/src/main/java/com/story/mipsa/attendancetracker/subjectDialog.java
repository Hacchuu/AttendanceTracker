package com.story.mipsa.attendancetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatDialogFragment;

public class subjectDialog extends AppCompatDialogFragment {

    public interface onInput {
        void sendInput(String input);
    }

    public subjectDialog.onInput onInput;

    private EditText editText;
    private Button cancel, Add;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;
    AttendanceTarget attendanceTarget;
    String email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();

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

                String subject = editText.getText().toString().trim();
                if (!subject.equals("")) {

                    user = firebaseAuth.getCurrentUser();
                    DatabaseReference userRef = ref.child("Users");
                    String sName = subject;
                    userRef.child(user.getUid()).child("Subjects").child(sName).setValue(subject);

                    onInput.sendInput(subject);
                }
                getDialog().dismiss();
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


}
