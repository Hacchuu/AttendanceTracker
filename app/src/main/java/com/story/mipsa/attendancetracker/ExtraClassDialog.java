package com.story.mipsa.attendancetracker;

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
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatDialogFragment;

import static android.content.Context.VIBRATOR_SERVICE;

public class ExtraClassDialog extends AppCompatDialogFragment {

    public interface OnInput2 {
        void sendExtraInput(String status, int position);
    }

    public ExtraClassDialog.OnInput2 onInput2;

    private RadioButton radioPresent, radioAbsent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.extra_class_dialog, container, false);
        TextView subject = view.findViewById(R.id.extraSubjectName);
        Button addExtra = view.findViewById(R.id.addExtra);
        radioPresent = view.findViewById(R.id.radioExtraPresent);
        radioAbsent = view.findViewById(R.id.radioExtraAbsent);

        Bundle bundle = getArguments();
        String subjectName = bundle.getString("SubjectName");
        final int position = bundle.getInt("position");
        subject.setText(subjectName.toUpperCase());


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
                    Toast.makeText(getActivity(),"Select an option", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onInput2 = (ExtraClassDialog.OnInput2) getActivity();
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
