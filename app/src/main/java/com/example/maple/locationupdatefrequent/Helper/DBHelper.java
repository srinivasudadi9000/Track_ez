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
        db.execSQL("CREATE TABLE IF NOT EXISTS messages(msg_id INTEGER PRIMARY KEY AUTOINCREMENT,msg VARCHAR,cdt VARCHAR,status VARCHAR);");
    }

    public void insertProject(String latitude, String longitude, String cdt,String address, String deviceid ,String deviceno,String status,Context context) {
        this.context = context;
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        db.execSQL("INSERT INTO checktime VALUES('" + latitude + "','" + longitude+"','"+ cdt + "','"+address+ "','"+deviceid+"','"+deviceno+"','"+status+"');");
        db.close();
        Log.d("dbcreated_insert","insertedsucess");
    }

    public void updateProject(String status, String cdt, Context context) {
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        String strSQL = "UPDATE checktime SET status = '"+status+"' WHERE cdt = '"+ cdt+"'";
        db.execSQL(strSQL);

        db.close();
        Log.d("checkupdated","checkupdatedsuccessfully");

    }

    public void insertMessage(String message, String cdt,String status,Context context) {
        this.context = context;
        db = context.openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
        db.execSQL("INSERT INTO messages VALUES('" + message + "','" + cdt+"','"+status+"');");
        db.close();
        Log.d("dbcreated_insert","insertedsucess");
    }

}
