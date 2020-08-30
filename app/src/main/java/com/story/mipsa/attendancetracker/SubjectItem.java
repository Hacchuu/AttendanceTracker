package com.story.mipsa.attendancetracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

//Custom class created to store details of attendance for each subject
public class SubjectItem implements Parcelable {
    private String subjectName;
    private int present;
    private int absent;
    private int total;
    private int bunk;
    private int attend;
    private float percentage;
    int id;
    private ArrayList<SubjectAttendanceDetails> subjectAttendanceDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubjectItem() {
    }

    public SubjectItem(String subjectName, int present, int absent, int total, float percentage, int bunk, int attend, int id, ArrayList<SubjectAttendanceDetails> attendanceDet) {
        this.subjectName = subjectName;
        this.present = present;
        this.absent = absent;
        this.total = total;
        this.percentage = percentage;
        this.bunk = bunk;
        this.attend = attend;
        this.id = id;
        if (attendanceDet == null)
            this.subjectAttendanceDetails = new ArrayList<SubjectAttendanceDetails>();
        else
            this.subjectAttendanceDetails = attendanceDet;
    }

    protected SubjectItem(Parcel in) {
        subjectName = in.readString();
        present = in.readInt();
        absent = in.readInt();
        total = in.readInt();
        bunk = in.readInt();
        attend = in.readInt();
        percentage = in.readFloat();
    }

    public static final Creator<SubjectItem> CREATOR = new Creator<SubjectItem>() {
        @Override
        public SubjectItem createFromParcel(Parcel in) {
            return new SubjectItem(in);
        }

        @Override
        public SubjectItem[] newArray(int size) {
            return new SubjectItem[size];
        }
    };

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<SubjectAttendanceDetails> getSubjectAttendanceDetails() {
        return subjectAttendanceDetails;
    }

    public void setSubjectAttendanceDetails(SubjectAttendanceDetails subjectAttendanceDetails) {
        if (this.subjectAttendanceDetails != null)
            this.subjectAttendanceDetails.add(subjectAttendanceDetails);
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
//        parcel.writeTypedObject(subjectAttendanceDetails, i);
    }

}
