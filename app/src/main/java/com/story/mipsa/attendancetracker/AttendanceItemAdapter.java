package com.story.mipsa.attendancetracker;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AttendanceItemAdapter extends RecyclerView.Adapter<AttendanceItemAdapter.ViewHolder> {
    private TimelineView timelineView;
    private TextView date;
    private TextView title;
    CardView cardView;
    private ArrayList<SubjectAttendanceDetails> subjectAttendanceDetailsList;
    private OnTimelimeListener onTimelimeListener;
    private boolean multiSelect = false;
    private ArrayList<SubjectAttendanceDetails> selectedItems = new ArrayList<>();

    public AttendanceItemAdapter(ArrayList<SubjectAttendanceDetails> subjectAttendanceDetailsList, OnTimelimeListener onTimelimeListener) {
        this.subjectAttendanceDetailsList = subjectAttendanceDetailsList;
        this.onTimelimeListener = onTimelimeListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnTimelimeListener onTimelimeListener;

        public ViewHolder(@NonNull View itemView, int viewType, OnTimelimeListener onTimelimeListener) {
            super(itemView);
            timelineView = itemView.findViewById(R.id.timeline);
            timelineView.initLine(viewType);
            cardView = itemView.findViewById(R.id.cardID);
            date = itemView.findViewById(R.id.timeline_date);
            title = itemView.findViewById(R.id.timeline_title);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SubjectAttendanceDetails currentDetails = subjectAttendanceDetailsList.get(position);
        date.setText(currentDetails.getDateOfEntry());
        title.setText(currentDetails.getStatus());
        String check = currentDetails.getStatus().toLowerCase().trim();
        if(currentDetails.getStatus().equalsIgnoreCase("absent")){
            timelineView.setMarkerColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return subjectAttendanceDetailsList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public interface OnTimelimeListener {
        void onTimelineClick(int position);
    }

}
