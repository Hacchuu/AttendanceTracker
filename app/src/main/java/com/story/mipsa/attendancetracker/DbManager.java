//package com.story.mipsa.attendancetracker;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import androidx.annotation.Nullable;
//
//public class DbManager extends SQLiteOpenHelper {
//
//    private static final String dbname = "AttendanceDetails.db";
//
//    public DbManager(@Nullable Context context) {
//        super(context, dbname, null, 1 );
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//
//        String qry = "create table details (enforce_one_row enum(1) not null unique default 'only', name text, minimumAttendance text, id int)";
////        String cleardbqry = "delete from details";
////        sqLiteDatabase.execSQL(cleardbqry);
//        sqLiteDatabase.execSQL(qry);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("drop table if exists details");
//        onCreate(sqLiteDatabase);
//    }
//
//    public String addRecord(String p1, String p2, int p3){
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put("name", p1);
//        contentValues.put("minimumAttendance", p2);
//        contentValues.put("id", p3);
//
//        long res = sqLiteDatabase.insert("details",null, contentValues);
//        if (res == -1)
//            return "Failed";
//        else
//            return "Successfully inserted";
//    }
//
//    public String updateRecordName(String p1, int p3){
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", p1);
//
//        contentValues.put("id", p3);
//        long res = sqLiteDatabase.update("details",contentValues,"id = ?", new String[] {String.valueOf(p3)});
//        if(res == -1)
//            return "Failure";
//        else
//            return "Success";
//    }
//
//    public String updateRecordTarget( String p2, int p3){
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("minimumAttendance", p2);
//        contentValues.put("id", p3);
//        long res = sqLiteDatabase.update("details",contentValues,"id = ?", new String[] {String.valueOf(p3)});
//        if(res == -1)
//            return "Failure";
//        else
//            return "Success";
//    }
//
//    public Cursor viewData(){
//        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
//
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from details",null);
//        return cursor;
//    }
//
//    public int getCount(){
//        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
//        int count = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase,"details");
//        sqLiteDatabase.close();
//        return count;
//    }
//
//}
