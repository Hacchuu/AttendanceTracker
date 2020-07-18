package com.story.mipsa.attendancetracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class AttendanceDetails implements Parcelable {
    private String status;
    private String dateOfEntry;

    public AttendanceDetails() {
    }

    public AttendanceDetails(String status, String dateOfEntry) {
        this.status = status;
        this.dateOfEntry = dateOfEntry;
    }

    protected AttendanceDetails(Parcel in) {
        status = in.readString();
    }

    public static final Creator<AttendanceDetails> CREATOR = new Creator<AttendanceDetails>() {
        @Override
        public AttendanceDetails createFromParcel(Parcel in) {
            return new AttendanceDetails(in);
        }

        @Override
        public AttendanceDetails[] newArray(int size) {
            return new AttendanceDetails[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateOfEntry() {
        return dateOfEntry;
    }

    public void setDateOfEntry(String dateOfEntry) {
        this.dateOfEntry = dateOfEntry;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
    }
}
