package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class extraClassEdit extends AppCompatDialogFragment {

    public interface onInput1 {
        void sendDetailsInput(String status, long date);
    }

    public extraClassEdit.onInput1 onInput1;
    private Button button, cancel;
    private RadioButton radioPresent, radioAbsent;
    private String status;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_extra_class_edit, container, false);
        button = view.findViewById(R.id.saveDetails);
        radioAbsent = view.findViewById(R.id.radioAbsent);
        radioPresent = view.findViewById(R.id.radioPresent);
        cancel = view.findViewById(R.id.cancelDetails);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioPresent.isChecked()) {
                    status = "Present";
                    onInput1.sendDetailsInput(status, 0);
                    getDialog().dismiss();
                } else if (radioAbsent.isChecked()) {
                    status = "Absent";
                    onInput1.sendDetailsInput(status, 0);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Select an option", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInput1 = (extraClassEdit.onInput1) getActivity();
        } catch (ClassCastException e) {

        }
    }
}
