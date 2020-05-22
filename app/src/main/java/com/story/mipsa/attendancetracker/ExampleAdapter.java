package com.story.mipsa.attendancetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> exampleItems;




    public static class ExampleViewHolder extends RecyclerView.ViewHolder{

        public TextView subjectName,Attendance,Status,Percentage;
        TextView optionDigit;
        public Button present,absent;
        MainActivity mainActivity = new MainActivity();
        public int presentS,presentTemp=0;
        public int absentS,total,totalS,attend=0,bunk=0, min,per;
        public float avg=0,temp;



        public ExampleViewHolder(View itemView) {
            super(itemView);
            present = itemView.findViewById(R.id.item_present);
            absent = itemView.findViewById(R.id.item_absent);
            subjectName = itemView.findViewById(R.id.SubName);
            Attendance = itemView.findViewById(R.id.item_number);
            Status = itemView.findViewById(R.id.item_displayStatus);
            Percentage = itemView.findViewById(R.id.item_displayPercentage);
            optionDigit = itemView.findViewById(R.id.txtOptionDigit);
            String target2 = "";
            String target = mainActivity.minimumAttendance;
            for(int i=0;i<3;i++){
                if(target.charAt(i) == '%') {
                    break;
                }
                else{
                    target2 = target2 + target.charAt(i);
                }
            }
            min = Integer.parseInt(target2);
        }
    }

    public ExampleAdapter(ArrayList<ExampleItem> exampleList){
        exampleItems = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_example,parent,false);
        ExampleViewHolder exampleViewHolder = new ExampleViewHolder(v);
        return exampleViewHolder;
    }


    //This function determines which item in the list we are currently looking at
    @Override
    public void onBindViewHolder(@NonNull  final ExampleViewHolder holder, int position) {
        final ExampleItem currentItem = exampleItems.get(position);

        holder.subjectName.setText(currentItem.getSubjectName());
        holder.Attendance.setText(currentItem.getPresent()+"/"+currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%",currentItem.getPercentage()));
        holder.Status.setText("Situation");
        holder.present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Present( currentItem, holder);
            }
        });
        holder.absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Absent( currentItem, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exampleItems.size();
    }

    public void Present(ExampleItem currentItem,ExampleAdapter.ExampleViewHolder holder){
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.presentS++;
        holder.total++;
        float avg = ((float)holder.presentS/(float)holder.total)*100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setPresent(holder.presentS);
        holder.Attendance.setText(currentItem.getPresent()+"/"+currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%",currentItem.getPercentage()));
        Calculate(currentItem,holder);
        if(holder.attend>0){
            if(holder.attend>1)
                holder.Status.setText("You can't bunk the next "+holder.attend+" classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        }
        else if(holder.bunk>0){
            if(holder.bunk>1)
                holder.Status.setText("You can bunk "+holder.bunk+" classes ♥‿♥");
            else
                holder.Status.setText("You can bunk 1 class (ᵔᴥᵔ)");
        }
    }

    public void Absent(ExampleItem currentItem, ExampleAdapter.ExampleViewHolder holder){
        holder.absentS = currentItem.getAbsent();
        holder.total = currentItem.getTotal();
        holder.absentS++;
        holder.total++;
        float avg = ((float)holder.presentS/(float)holder.total)*100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setAbsent(holder.absentS);
        holder.Attendance.setText(currentItem.getPresent()+"/"+currentItem.getTotal());
        holder.Percentage.setText(String.format("%.1f%%",currentItem.getPercentage()));
        Calculate(currentItem,holder);
        if(holder.attend>0){
            if(holder.attend>1)
                holder.Status.setText("You can't bunk the next "+holder.attend+" classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        }
        else if(holder.bunk>0){
            if(holder.bunk>1)
                holder.Status.setText("You can bunk "+holder.bunk+" classes ♥‿♥");
            else
                holder.Status.setText("You can bunk your next class (ᵔᴥᵔ)");
        }
    }

    public  void Calculate(ExampleItem currentItem, ExampleAdapter.ExampleViewHolder holder){
        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.totalS = holder.total;
        holder.attend = 0;
        holder.bunk = 0;
        if(holder.totalS != 0){
            holder.avg = ((float)holder.presentS/(float)holder.totalS)*100;
        }
        holder.temp = holder.avg;
        if(holder.temp >= holder.min) {
            do {
                holder.totalS += 1;
                holder.temp = ((float) holder.presentS / (float) holder.totalS) * 100;
                if (holder.temp < holder.min && holder.bunk == 0) {
                    holder.attend++;
                } else if (holder.temp >= holder.min && holder.attend == 0)
                    holder.bunk++;
            } while (holder.temp > holder.min);
        }
        else{
            holder.presentTemp = holder.presentS;
            do {
                holder.totalS += 1;
                holder.presentTemp+=1;
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
}


