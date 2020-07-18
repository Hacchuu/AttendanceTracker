package com.story.mipsa.attendancetracker;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttendanceItemAdapter extends RecyclerView.Adapter<AttendanceItemAdapter.ViewHolder> {
    public TimelineView timelineView;
    TextView date;
    TextView title;
    private ArrayList<AttendanceDetails> attendanceDetailsList;
    private OnTimelimeListener onTimelimeListener;

    public AttendanceItemAdapter(ArrayList<AttendanceDetails> attendanceDetailsList, OnTimelimeListener onTimelimeListener) {
        this.attendanceDetailsList = attendanceDetailsList;
        this.onTimelimeListener = onTimelimeListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnTimelimeListener onTimelimeListener;

        public ViewHolder(@NonNull View itemView, int viewType, OnTimelimeListener onTimelimeListener) {
            super(itemView);
            timelineView = itemView.findViewById(R.id.timeline);
            timelineView.initLine(viewType);
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
        final AttendanceDetails currentDetails = attendanceDetailsList.get(position);
        date.setText(currentDetails.getDateOfEntry());
        title.setText(currentDetails.getStatus());
    }

    @Override
    public int getItemCount() {
        return attendanceDetailsList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public interface OnTimelimeListener {
        void onTimelineClick(int position);
    }

}
