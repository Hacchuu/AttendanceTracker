package com.story.mipsa.attendancetracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.progresviews.ProgressWheel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.VIBRATOR_SERVICE;


public class SubjectItemAdapter extends RecyclerView.Adapter<SubjectItemAdapter.ExampleViewHolder>{
    private ArrayList<SubjectItem> subjectItems;
    private FragmentActivity context;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private OnItemListener onItemListener;
    private boolean multiSelect = false;
    private ArrayList<SubjectItem> selectedItems = new ArrayList();
    MainActivity mainActivity;
    private long currentDate;

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

            if(menuItem.getItemId() == R.id.action_delete){
                shakeItBaby();
                for(int i = 0; i < selectedItems.size(); i++){
                    subjectItems.remove(selectedItems.get(i));
                }

                DatabaseReference checkRef = ref.child("Users").child(user.getUid()).child("Subjects");
                checkRef.setValue(subjectItems);
                userRef.keepSynced(true);
                Toast.makeText(context,"Selected cards deleted",Toast.LENGTH_SHORT).show();
                actionMode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedItems.clear();
            user = firebaseAuth.getCurrentUser();
            DatabaseReference checkRef = ref.child("Users").child(user.getUid()).child("Subjects");
            checkRef.setValue(subjectItems);
            checkRef.keepSynced(true);
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            context.startActivity(new Intent(context, MainActivity.class));
            context.finish();
        }
    };


    public SubjectItemAdapter(ArrayList<SubjectItem> exampleList, FragmentActivity context, OnItemListener onItemListener) {
        subjectItems = exampleList;
        this.context = context;
        this.onItemListener = onItemListener;
        mainActivity = (MainActivity) context;
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView subjectName, attendance, status;
        private ImageView addExtraImage;
        private Button present, absent;
        MainActivity mainActivity = new MainActivity();
        private int presentS, presentTemp = 0;
        private int absentS, total, totalS, attend = 0, bunk = 0, min, per;
        private float avg = 0, temp;
        private OnItemListener onItemListener;
        private ProgressWheel progressWheelGreen;
        private ProgressWheel progressWheelRed;



        //Custom view Holder that describes the items in the recycler view element
        public ExampleViewHolder(final View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            present = itemView.findViewById(R.id.item_present);
            absent = itemView.findViewById(R.id.item_absent);
            subjectName = itemView.findViewById(R.id.nameSubject);
            attendance = itemView.findViewById(R.id.item_number);
            status = itemView.findViewById(R.id.item_displayStatus);
            progressWheelGreen = itemView.findViewById(R.id.wheelprogressGreen);
            progressWheelRed = itemView.findViewById(R.id.wheelprogressRed);
            addExtraImage = itemView.findViewById(R.id.addExtra2);


            StringBuilder target2 = new StringBuilder();

            String target = MainActivity.getMinimumAttendance();
            for (int i = 0; i < 3; i++) {
                if (target.charAt(i) == '%') {
                    break;
                } else {
                    target2.append(target.charAt(i));
                }
            }
            min = Integer.parseInt(target2.toString());

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
        return new ExampleViewHolder(v, onItemListener);
    }

    @Override
    public long getItemId(int position) {
        return subjectItems.get(position).getId();
}

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //This function determines which item in the list we are currently looking at
    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder holder, final int position) {
        holder.itemView.setAlpha(1f);
        final SubjectItem currentItem = subjectItems.get(position);
        String ind = String.valueOf(subjectItems.indexOf(currentItem));
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        ref = database.getReference().getRoot();

        holder.addExtraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExtraClassDialog dialog = new ExtraClassDialog();
                Bundle bundle = new Bundle();
                bundle.putString("SubjectName", currentItem.getSubjectName());
                bundle.putInt("position", position);
                dialog.setArguments(bundle);
                dialog.show((context).getSupportFragmentManager(), "ExtraClassDialog");
            }
        });

        if(selectedItems.contains(currentItem)){
            holder.itemView.setAlpha(0.5f);
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



//        holder holds the pointer to the current item in the recycler view
        holder.subjectName.setText(currentItem.getSubjectName());
        holder.attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        if(currentItem.getPercentage() >= holder.min){
            holder.progressWheelRed.setVisibility(View.INVISIBLE);
            holder.progressWheelGreen.setVisibility(View.VISIBLE);
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        else{
            holder.progressWheelGreen.setVisibility(View.INVISIBLE);
            holder.progressWheelRed.setVisibility(View.VISIBLE);
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        if(currentItem.getTotal() != 0) {
            calculate(currentItem, holder);
            if (currentItem.getAttend() > 0) {
                if (currentItem.getAttend() > 1)
                    holder.status.setText("You can't bunk the next " + currentItem.getAttend() + " classes ⚆_⚆");
                else
                    holder.status.setText("You can't bunk the next class ◉_◉");
            } else if (currentItem.getBunk() > 0) {
                if (currentItem.getBunk() > 1)
                    holder.status.setText("You can bunk " + currentItem.getBunk() + " classes (¬‿¬)");
                else
                    holder.status.setText("You can bunk 1 class ^.^");
            }
        }
        //When present button is pressed
        holder.present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Present(currentItem, holder);
                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                String sub = currentItem.getSubjectName();
                userRef.child(user.getUid()).child("Subjects").child(ind).setValue(currentItem);
            }
        });



        //When absent button is pressed
        holder.absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Absent(currentItem, holder);
                user = firebaseAuth.getCurrentUser();DatabaseReference userRef = ref.child("Users");
                String sub = currentItem.getSubjectName();
                userRef.child(user.getUid()).child("Subjects").child(ind).setValue(currentItem);
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
    public void Present(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder){
        currentDate = mainActivity.getSelectedDate();
        ArrayList<SubjectAttendanceDetails> attendanceList = currentItem.getSubjectAttendanceDetails();
        int check = checkExisiting(attendanceList, currentDate);
        if(check == 1){
            return;
        }
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.presentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setPresent(holder.presentS);
        currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Present", currentDate, false, attendanceList.size() + 1));

        holder.attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        if(currentItem.getPercentage() >= holder.min){
            holder.progressWheelRed.setVisibility(View.INVISIBLE);
            holder.progressWheelGreen.setVisibility(View.VISIBLE);
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        else{
            holder.progressWheelGreen.setVisibility(View.INVISIBLE);
            holder.progressWheelRed.setVisibility(View.VISIBLE);
               holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.status.setText("You can bunk " + holder.bunk + " classes (¬‿¬)");
            else if(holder.bunk == 1)
                holder.status.setText("You can bunk 1 class ^.^");
        }
        }


    //Function to calculate all variables when absent is entered
    public void Absent(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
        currentDate = mainActivity.getSelectedDate();
        ArrayList<SubjectAttendanceDetails> attendanceList = currentItem.getSubjectAttendanceDetails();
        int check = checkExisiting(attendanceList, currentDate);
        if(check == 1)
            return;

        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.absentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setAbsent(holder.absentS);
        holder.attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        if(currentItem.getPercentage() >= holder.min){
            holder.progressWheelRed.setVisibility(View.INVISIBLE);
            holder.progressWheelGreen.setVisibility(View.VISIBLE);
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        else{
            holder.progressWheelGreen.setVisibility(View.INVISIBLE);
            holder.progressWheelRed.setVisibility(View.VISIBLE);
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Absent", currentDate,false, attendanceList.size()+1));
        calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.status.setText("You can bunk " + holder.bunk + " classes ♥‿♥");
            else if(holder.bunk == 1)
                holder.status.setText("You can bunk your next class (ᵔᴥᵔ)");
        }
    }

    private int checkExisiting(ArrayList<SubjectAttendanceDetails> attendanceList, long currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        String currentDate1 = sdf.format(currentDate);
        for(int i=0; i<attendanceList.size();i++) {
            String checkDate = sdf.format(attendanceList.get(i).getDateOfEntry());
            long checkD = attendanceList.get(i).getDateOfEntry();
            boolean extraClass = attendanceList.get(i).isExtraClass();
            if(checkDate.equalsIgnoreCase(currentDate1)){
                if(extraClass){
                    continue;
                }
                else{
                    Toast.makeText(mainActivity, "Already entered attendance for\n" + currentDate1, Toast.LENGTH_LONG).show();
                    return 1;
                }
            }
        }
        return 0;
    }

    //Calculate the prediction of number of classes to bunk or attend
    public void calculate(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.totalS = currentItem.getTotal();
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
        }
        else {
            holder.attend = 1;
            holder.presentTemp = holder.presentS;
            while(holder.temp <= holder.min) {
                holder.totalS += 1;
                holder.presentTemp += 1;
                holder.temp = ((float) holder.presentTemp / (float) holder.totalS) * 100;
                if (holder.temp <= holder.min && holder.bunk == 0) {
                    holder.attend++;
                } else if (holder.temp > holder.min && holder.attend == 0)
                    holder.bunk++;
            }
        }
        currentItem.setAttend(holder.attend);
        currentItem.setBunk(holder.bunk);
    }

    //Vibration on click method
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


