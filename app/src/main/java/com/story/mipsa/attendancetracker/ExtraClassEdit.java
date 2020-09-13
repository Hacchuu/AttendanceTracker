package com.story.mipsa.attendancetracker;

import androidx.appcompat.app.AppCompatDialogFragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;

public class ExtraClassEdit extends AppCompatDialogFragment {

    public interface OnInput1 {
        void sendDetailsInput(String status, long date);
    }

    public ExtraClassEdit.OnInput1 onInput1;
    private Button button;
    private RadioButton radioPresent, radioAbsent;
    private String status;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_extra_class_edit, container, false);
        button = view.findViewById(R.id.saveDetails);
        radioAbsent = view.findViewById(R.id.radioAbsent);
        radioPresent = view.findViewById(R.id.radioPresent);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeItBaby();
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
            onInput1 = (ExtraClassEdit.OnInput1) getActivity();
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
