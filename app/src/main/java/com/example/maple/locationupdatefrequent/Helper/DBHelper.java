package com.example.maple.locationupdatefrequent.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {
    static SQLiteDatabase db;
    static Context context;

    public DBHelper(Context context) {
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS checktime(latitude VARCHAR,longitude VARCHAR,cdt VARCHAR,address VARCHAR,deviceid VARCHAR,deviceno VARCHAR,status VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS messages(latitude VARCHAR,longitude VARCHAR,msg VARCHAR,cdt VARCHAR,status VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS dailyreports(latitude VARCHAR,longitude VARCHAR,msg VARCHAR,cdt VARCHAR,rep_date VARCHAR,imagepath VARCHAR,status VARCHAR);");
        //db.execSQL("CREATE TABLE IF NOT EXISTS questions(centers VARCHAR,params VARCHAR);");
    }

    public DBHelper() {

    }
    public void insertquestions(String centers, String params, Context context) {
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        DBHelper.context = context;
        db.execSQL("INSERT INTO questions VALUES('" + centers + "','" + params+ "');");
        db.close();
        Log.d("dbcreated_insert", "insertedsucess");
    }

    public void insertReport(String latitude, String longitude, String message, String cdt, String rep_date, String imagepath, String status, Context context) {
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        DBHelper.context = context;
        db.execSQL("INSERT INTO dailyreports VALUES('" + latitude + "','" + longitude + "','" + message + "','" + cdt + "','" + rep_date + "','" + imagepath + "','" + status + "');");
        db.close();
        Log.d("dbcreated_insert", "insertedsucess");
    }


    public void insertProject(String latitude, String longitude, String cdt, String address, String deviceid, String deviceno, String status, Context context) {
        DBHelper.context = context;
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        db.execSQL("INSERT INTO checktime VALUES('" + latitude + "','" + longitude + "','" + cdt + "','" + address + "','" + deviceid + "','" + deviceno + "','" + status + "');");
        db.close();
        Log.d("dbcreated_insert", "insertedsucess");
    }

    public void updateProject(String status, String cdt, Context context) {
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        String strSQL = "UPDATE checktime SET status = '" + status + "' WHERE cdt = '" + cdt + "'";
        db.execSQL(strSQL);

        db.close();
        Log.d("checkupdated", "checkupdatedsuccessfully");

    }


    public void insertMessage(String latitude, String longitude, String message, String cdt, String status, Context context) {
        DBHelper.context = context;
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        db.execSQL("INSERT INTO messages VALUES('" + latitude + "','" + longitude + "','" + message + "','" + cdt + "','" + status + "');");
        db.close();
        Log.d("dbcreated_insert", "insertedsucess");
    }

    public void updateMessage(String status, String cdt, Context context) {
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        String strSQL = "UPDATE messages SET status = '" + status + "' WHERE cdt = '" + cdt + "'";
        db.execSQL(strSQL);

        db.close();
        Log.d("checkupdated", "checkupdatedsuccessfully");

    }


}
