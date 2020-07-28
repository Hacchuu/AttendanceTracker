package com.story.mipsa.attendancetracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
//import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.VIBRATOR_SERVICE;


public class SubjectItemAdapter extends RecyclerView.Adapter<SubjectItemAdapter.ExampleViewHolder> {
    private ArrayList<SubjectItem> subjectItems;
    private FragmentActivity context;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    private OnItemListener onItemListener;
    private boolean multiSelect = false;
    private ArrayList<SubjectItem> selectedItems = new ArrayList();
    MainActivity mainActivity;
    int flag;

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            user = firebaseAuth.getCurrentUser();
            DatabaseReference userRef = ref.child("Users");
//            String sub = currentItem.getSubjectName();
           flag = 0;

            if(menuItem.getItemId() == R.id.action_delete){
                flag = 1;
                for(int i=0; i<selectedItems.size();i++){
                    subjectItems.remove(selectedItems.get(i));
                    String sub = selectedItems.get(i).getSubjectName();
                    userRef.child(user.getUid()).child("Subjects").child(sub).removeValue();
                }
                Toast.makeText(context,"Selected cards deleted",Toast.LENGTH_SHORT);
                actionMode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedItems.clear();
            if(flag == 1){
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                context.startActivity(new Intent(context,MainActivity.class));
                context.finish();
            }
            else {
                notifyDataSetChanged();
            }
        }
    };

    public SubjectItemAdapter(ArrayList<SubjectItem> exampleList, FragmentActivity context, OnItemListener onItemListener) {
        subjectItems = exampleList;
        this.context = context;
        this.onItemListener = onItemListener;
        mainActivity = (MainActivity) context;
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

        //Custom view Holder that describes the items in the recycler view element
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

            String target = mainActivity.getMinimumAttendance();
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
        holder.itemView.setAlpha(1f);
        final SubjectItem currentItem = subjectItems.get(position);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ref = database.getReference().getRoot();

        if(selectedItems.contains(currentItem)){
            holder.itemView.setAlpha(0.5f);
//            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!multiSelect){
                    shakeItBaby();
                    multiSelect = true;
                    mainActivity.startActionMode(callback);
                    selectItems(holder,currentItem);
                }
//                if(multiSelect){
//                    selectItems(holder,currentItem);
//                }
                return true;
            }


        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(multiSelect){
                    shakeItBaby();
                    selectItems(holder,currentItem);
                }
                else{
                    onItemListener.OnItemClick(holder.getAdapterPosition());
                }
            }
        });



        //holder holds the pointer to the current item in the recycler view
        holder.subjectName.setText(currentItem.getSubjectName());
        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%", currentItem.getPercentage()));
        if(currentItem.getTotal() != 0)
            Calculate(currentItem, holder);
        Log.d("check", ""+currentItem.getAttend()+"---"+currentItem.getBunk());
        if (currentItem.getAttend() > 0) {
            if (currentItem.getAttend() > 1)
                holder.Status.setText("You can't bunk the next " + currentItem.getAttend() + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (currentItem.getBunk() > 0) {
            if (currentItem.getBunk() > 1)
                holder.Status.setText("You can bunk " + currentItem.getBunk() + " classes (¬‿¬)");
            else
                holder.Status.setText("You can bunk 1 class ^.^");
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


    private void selectItems(ExampleViewHolder holder, SubjectItem currentItem) {
        if(selectedItems.contains(currentItem)){
            selectedItems.remove(currentItem);
            holder.itemView.setAlpha(1.0f);
        }
        else {
            selectedItems.add(currentItem);
            holder.itemView.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() {
        return subjectItems.size();
    }

    //Function to calculate all variables when present is enetered
    public void Present(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.presentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setPresent(holder.presentS);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        String currentDate = sdf.format(new Date());
        Log.d("harsh AD check", "" + new SubjectAttendanceDetails("Present", currentDate));
        currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Present", currentDate));
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
                holder.Status.setText("You can bunk " + holder.bunk + " classes (¬‿¬)");
            else if(holder.bunk == 1)
                holder.Status.setText("You can bunk 1 class ^.^");

        }
    }

    //Function to calculate all variables when absent is entered
    public void Absent(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.absentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setAbsent(holder.absentS);
        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%", currentItem.getPercentage()));
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        String currentDate = sdf.format(new Date());
        currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Absent", currentDate));
        Calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.Status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.Status.setText("You can bunk " + holder.bunk + " classes ♥‿♥");
            else if(holder.bunk == 1)
                holder.Status.setText("You can bunk your next class (ᵔᴥᵔ)");
        }
    }

    //Calculate the prediction of number of classes to bunk or attend
    public void Calculate(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
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

    //Vibration on clikc method
    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(125);
        }
    }

    //Interface to send the position of the current item element
    public interface OnItemListener {
        void OnItemClick(int position);
    }


}


