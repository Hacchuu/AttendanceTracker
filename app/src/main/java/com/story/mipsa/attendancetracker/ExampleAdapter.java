package com.story.mipsa.attendancetracker;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> exampleItems;
    private FragmentActivity context;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    private OnItemListener onItemListener;

    public ExampleAdapter(ArrayList<ExampleItem> exampleList, FragmentActivity context, OnItemListener onItemListener) {
        exampleItems = exampleList;
        this.context = context;
        this.onItemListener = onItemListener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView subjectName, Attendance, Status, Percentage;
        TextView optionDigit;
        public Button present, absent;
        MainActivity mainActivity = new MainActivity();
        public int presentS, presentTemp = 0;
        public int absentS, total, totalS, attend = 0, bunk = 0, min, per;
        public float avg = 0, temp;
        private OnItemListener onItemListener;

        public ExampleViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            present = itemView.findViewById(R.id.item_present);
            absent = itemView.findViewById(R.id.item_absent);
            subjectName = itemView.findViewById(R.id.nameSubject);
            Attendance = itemView.findViewById(R.id.item_number);
            Status = itemView.findViewById(R.id.item_displayStatus);
            Percentage = itemView.findViewById(R.id.item_displayPercentage);
            optionDigit = itemView.findViewById(R.id.txtOptionDigit);
            String target2 = "";

            String target = mainActivity.minimumAttendance;
            for (int i = 0; i < 3; i++) {
                if (target.charAt(i) == '%') {
                    break;
                } else {
                    target2 = target2 + target.charAt(i);
                }
            }
            min = Integer.parseInt(target2);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.OnItemClick(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_example, parent, false);
        ExampleViewHolder exampleViewHolder = new ExampleViewHolder(v, onItemListener);
        return exampleViewHolder;
    }


    //This function determines which item in the list we are currently looking at
    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder holder, int position) {
        final ExampleItem currentItem = exampleItems.get(position);
//           currentItem.setAttendanceDetails(d);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ref = database.getReference().getRoot();

        holder.subjectName.setText(currentItem.getSubjectName());
        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%", currentItem.getPercentage()));


        if (currentItem.getAttend() > 0) {
            if (currentItem.getAttend() > 1)
                holder.Status.setText("You can't bunk the next " + currentItem.getAttend() + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (currentItem.getBunk() > 0) {
            if (currentItem.getBunk() > 1)
                holder.Status.setText("You can bunk " + currentItem.getBunk() + " classes ♥‿♥");
            else
                holder.Status.setText("You can bunk 1 class (ᵔᴥᵔ)");
        }

        //When present button is pressed
        holder.present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Present(currentItem, holder);
                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                String sub = currentItem.getSubjectName();
                userRef.child(user.getUid()).child("Subjects").child(sub).setValue(currentItem);
            }
        });

        //When absent button is pressed
        holder.absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Absent(currentItem, holder);
                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                String sub = currentItem.getSubjectName();
                userRef.child(user.getUid()).child("Subjects").child(sub).setValue(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exampleItems.size();
    }

    //Function to calculate all variables when present is enetered
    public void Present(ExampleItem currentItem, ExampleAdapter.ExampleViewHolder holder) {
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.presentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setPresent(holder.presentS);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        String currentDate = sdf.format(new Date());
        Log.d("harsh AD check", "" + new AttendanceDetails("Present", currentDate));
        currentItem.setAttendanceDetails(new AttendanceDetails("Present", currentDate));

        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%", currentItem.getPercentage()));
        Calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.Status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.Status.setText("You can bunk " + holder.bunk + " classes ♥‿♥");
            else
                holder.Status.setText("You can bunk 1 class (ᵔᴥᵔ)");
        }
    }

    //Function to calculate all variables when absent is entered
    public void Absent(ExampleItem currentItem, ExampleAdapter.ExampleViewHolder holder) {
        holder.absentS = currentItem.getAbsent();
        holder.total = currentItem.getTotal();
        holder.absentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setAbsent(holder.absentS);
        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%", currentItem.getPercentage()));

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        String currentDate = sdf.format(new Date());

        currentItem.setAttendanceDetails(new AttendanceDetails("Absent", currentDate));

        Calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.Status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.Status.setText("You can bunk " + holder.bunk + " classes ♥‿♥");
            else
                holder.Status.setText("You can bunk your next class (ᵔᴥᵔ)");
        }
    }

    //Calculate the prediction of number of classes to bunk or attend
    public void Calculate(ExampleItem currentItem, ExampleAdapter.ExampleViewHolder holder) {
        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.totalS = holder.total;
        holder.attend = 0;
        holder.bunk = 0;
        if (holder.totalS != 0) {
            holder.avg = ((float) holder.presentS / (float) holder.totalS) * 100;
        }
        holder.temp = holder.avg;
        if (holder.temp >= holder.min) {
            do {
                holder.totalS += 1;
                holder.temp = ((float) holder.presentS / (float) holder.totalS) * 100;
                if (holder.temp < holder.min && holder.bunk == 0) {
                    holder.attend++;
                } else if (holder.temp >= holder.min && holder.attend == 0)
                    holder.bunk++;
            } while (holder.temp > holder.min);
        } else {
            holder.presentTemp = holder.presentS;
            do {
                holder.totalS += 1;
                holder.presentTemp += 1;
                holder.temp = ((float) holder.presentTemp / (float) holder.totalS) * 100;
                if (holder.temp <= holder.min && holder.bunk == 0) {
                    holder.attend++;
                } else if (holder.temp > holder.min && holder.attend == 0)
                    holder.bunk++;
            } while (holder.temp <= holder.min);
        }
        currentItem.setAttend(holder.attend);
        currentItem.setBunk(holder.bunk);
    }

    //Interface to send the position of the current item element
    public interface OnItemListener {
        void OnItemClick(int position);
    }
}


