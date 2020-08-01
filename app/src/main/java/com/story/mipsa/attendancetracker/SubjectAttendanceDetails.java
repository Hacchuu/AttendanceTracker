package com.story.mipsa.attendancetracker;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectAttendanceDetails implements Parcelable {
    private String status;
    private long dateOfEntry;
    boolean extraClass;

    public SubjectAttendanceDetails() {
    }

    public boolean isExtraClass() {
        return extraClass;
    }

    public void setExtraClass(boolean extraClass) {
        this.extraClass = extraClass;
    }

    public SubjectAttendanceDetails(String status, long dateOfEntry, boolean extraClass) {
        this.status = status;
        this.dateOfEntry = dateOfEntry;
        this.extraClass = extraClass;
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

    public long getDateOfEntry() {
        return dateOfEntry;
    }

    public void setDateOfEntry(long dateOfEntry) {
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
