package com.story.mipsa.attendancetracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vipulasri.timelineview.TimelineView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.VIBRATOR_SERVICE;

public class AttendanceItemAdapter extends RecyclerView.Adapter<AttendanceItemAdapter.ViewHolder> {
    private final FragmentActivity context;
    private TimelineView timelineView;
    private TextView date;
    private TextView title;
    CardView cardView;
    private ArrayList<SubjectAttendanceDetails> subjectAttendanceDetailsList;
    private OnTimelimeListener onTimelimeListener;
    private boolean multiSelect = false;
    private ArrayList<SubjectAttendanceDetails> selectedItems = new ArrayList<>();
    int flag = 0;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference ref;
    SubjectDetails subjectDetails;

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
            SubjectItem currentItem = subjectDetails.getCurrentSubjectItem();
            String ind = subjectDetails.getIndex();
            flag = 0;
            if(menuItem.getItemId() == R.id.action_delete){
                shakeItBaby();
                flag = 1;
                String sub = currentItem.getSubjectName();
                for(int i=0; i<selectedItems.size();i++){
                    int index = subjectAttendanceDetailsList.indexOf(selectedItems.get(i));
                    subjectAttendanceDetailsList.remove(selectedItems.get(i));
                    userRef.child(user.getUid()).child("Subjects").child(ind).child("subjectAttendanceDetails").child(Integer.toString(index)).removeValue();
                    if(selectedItems.get(i).getStatus().equalsIgnoreCase("Absent")){
                        currentItem.setAbsent(currentItem.getAbsent()-1);
                        currentItem.setTotal(currentItem.getTotal()-1);
                    }
                    else if(selectedItems.get(i).getStatus().equalsIgnoreCase("Present")){
                        currentItem.setPresent(currentItem.getPresent()-1);
                        currentItem.setTotal(currentItem.getTotal()-1);
                    }
                }

                Toast.makeText(context,"Selected cards deleted",Toast.LENGTH_SHORT).show();
                actionMode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            user = firebaseAuth.getCurrentUser();
            DatabaseReference userRef = ref.child("Users");
            SubjectItem currentItem = subjectDetails.getCurrentSubjectItem();
            String sub = currentItem.getSubjectName();
            String ind = subjectDetails.getIndex();

            selectedItems.clear();
            subjectDetails.Recalculate();
            subjectDetails.setViews();

            userRef.child(user.getUid()).child("Subjects").child(ind).setValue(currentItem);
            userRef.child(user.getUid()).child("Subjects").child(ind).child("subjectAttendanceDetails").setValue(subjectAttendanceDetailsList);

            Intent intent = new Intent(context,SubjectDetails.class);
            intent.putExtra("Selected Subject Item",subjectDetails.getCurrentSubjectItem());
            intent.putExtra("index",subjectDetails.getIndex());
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            context.startActivity(intent);
            context.finish();

        }
    };



    public AttendanceItemAdapter(ArrayList<SubjectAttendanceDetails> subjectAttendanceDetailsList, FragmentActivity context, OnTimelimeListener onTimelimeListener) {
        this.subjectAttendanceDetailsList = subjectAttendanceDetailsList;
        this.context = context;
        this.onTimelimeListener = onTimelimeListener;
        subjectDetails = (SubjectDetails)context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnTimelimeListener onTimelimeListener;

        public ViewHolder(@NonNull View itemView, int viewType, OnTimelimeListener onTimelimeListener) {
            super(itemView);
            timelineView = itemView.findViewById(R.id.timeline);
//            timelineView.initLine(viewType);

            cardView = itemView.findViewById(R.id.cardID);
            date = itemView.findViewById(R.id.timeline_date);
            title = itemView.findViewById(R.id.timeline_title);
//            setIsRecyclable(false);
            this.onTimelimeListener = onTimelimeListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTimelimeListener.onTimelineClick(getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.details_item, null);
        return new ViewHolder(view, viewType, onTimelimeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ref = database.getReference().getRoot();
        final SubjectAttendanceDetails currentDetails = subjectAttendanceDetailsList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        String currentDate = sdf.format(currentDetails.getDateOfEntry());
        date.setText(currentDate);
        title.setText(currentDetails.getStatus()+"                             ");
        if(currentDetails.isExtraClass()){
            cardView.setCardBackgroundColor(Color.parseColor("#CFD8DC"));
            title.setText(currentDetails.getStatus()+" - Extra Class       ");
        }
        else
            cardView.setCardBackgroundColor(Color.WHITE);

        if(currentDetails.getStatus().equalsIgnoreCase("absent")){
            timelineView.setMarkerColor(Color.RED);
        }
        else if(currentDetails.getStatus().equalsIgnoreCase("present")){
            timelineView.setMarkerColor(Color.parseColor("#228B22"));
        }

        if(selectedItems.contains(currentDetails)){
            holder.itemView.setAlpha(0.5f);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!multiSelect){
                    shakeItBaby();
                    multiSelect = true;
                    subjectDetails.startActionMode(callback);
                    selectItems(holder,currentDetails);
                }
                return true;
            }


        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(multiSelect){
                    selectItems(holder,currentDetails);
                }
                else{
                    onTimelimeListener.onTimelineClick(holder.getAdapterPosition());
                }
            }
        });

    }
    private void selectItems(AttendanceItemAdapter.ViewHolder holder, SubjectAttendanceDetails currentItem) {
        if(selectedItems.contains(currentItem)){
            selectedItems.remove(currentItem);
            holder.itemView.setAlpha(1.0f);
        }
        else {
            selectedItems.add(currentItem);
            holder.itemView.setAlpha(0.5f);
        }
    }

    //Vibration on click method
    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(125);
        }
    }


    @Override
    public int getItemCount() {
        return subjectAttendanceDetailsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
//        return subjectAttendanceDetailsList.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
//        return TimelineView.getTimeLineViewType(position, getItemCount());
//        return subjectAttendanceDetailsList.get(position).getId();
    }


    public interface OnTimelimeListener {
        void onTimelineClick(int position);
    }

}
