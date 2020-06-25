package com.story.mipsa.attendancetracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExampleItem implements Parcelable {
    private String subjectName;
    private int present;
    private int absent;
    private int total;
    private int bunk;
    private int attend;
    private float percentage;
    private ArrayList<AttendanceDetails> attendanceDetails;

    public ExampleItem(){
    }

    public ExampleItem(String subjectName, int present, int absent, int total, float percentage, int bunk, int attend, ArrayList<AttendanceDetails> attendanceDet) {
        this.subjectName = subjectName;
        this.present = present;
        this.absent = absent;
        this.total = total;
        this.percentage = percentage;
        this.bunk = bunk;
        this.attend = attend;
        if(attendanceDet == null)
            this.attendanceDetails = new ArrayList<AttendanceDetails>();
        else
            this.attendanceDetails = attendanceDet;
    }

    protected ExampleItem(Parcel in) {
        subjectName = in.readString();
        present = in.readInt();
        absent = in.readInt();
        total = in.readInt();
        bunk = in.readInt();
        attend = in.readInt();
        percentage = in.readFloat();
    }

    public static final Creator<ExampleItem> CREATOR = new Creator<ExampleItem>() {
        @Override
        public ExampleItem createFromParcel(Parcel in) {
            return new ExampleItem(in);
        }

        @Override
        public ExampleItem[] newArray(int size) {
            return new ExampleItem[size];
        }
    };

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<AttendanceDetails> getAttendanceDetails() {
        return attendanceDetails;
    }

    public void setAttendanceDetails(AttendanceDetails attendanceDetails) {
//        this.attendanceDetails = attendanceDetails;
        if(this.attendanceDetails != null)
            this.attendanceDetails.add(attendanceDetails);
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getBunk() {
        return bunk;
    }

    public void setBunk(int bunk) {
        this.bunk = bunk;
    }

    public int getAttend() {
        return attend;
    }

    public void setAttend(int attend) {
        this.attend = attend;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subjectName);
        parcel.writeInt(present);
        parcel.writeInt(absent);
        parcel.writeInt(total);
        parcel.writeInt(bunk);
        parcel.writeInt(attend);
        parcel.writeFloat(percentage);
//        parcel.writeTypedObject(attendanceDetails, i);
    }

}
