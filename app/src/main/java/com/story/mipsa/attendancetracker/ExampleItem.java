package com.story.mipsa.attendancetracker;

public class ExampleItem {
    private String subjectName;
    private int present;
    private int absent;
    private int total;
    private int bunk;
    private int attend;
    private float percentage;

    public ExampleItem(){
    }

    public ExampleItem( String subjectName, int present, int absent, int total, float percentage, int bunk, int attend) {
        this.subjectName = subjectName;
        this.present = present;
        this.absent = absent;
        this.total = total;
        this.percentage = percentage;
        this.bunk = bunk;
        this.attend = attend;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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
}
