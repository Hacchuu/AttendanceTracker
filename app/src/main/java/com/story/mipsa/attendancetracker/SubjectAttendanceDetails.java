package com.story.mipsa.attendancetracker;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectAttendanceDetails implements Parcelable {
    private String status;
    private String dateOfEntry;

    public SubjectAttendanceDetails() {
    }

    public SubjectAttendanceDetails(String status, String dateOfEntry) {
        this.status = status;
        this.dateOfEntry = dateOfEntry;
    }

    protected SubjectAttendanceDetails(Parcel in) {
        status = in.readString();
    }

    public static final Creator<SubjectAttendanceDetails> CREATOR = new Creator<SubjectAttendanceDetails>() {
        @Override
        public SubjectAttendanceDetails createFromParcel(Parcel in) {
            return new SubjectAttendanceDetails(in);
        }

        @Override
        public SubjectAttendanceDetails[] newArray(int size) {
            return new SubjectAttendanceDetails[size];
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
