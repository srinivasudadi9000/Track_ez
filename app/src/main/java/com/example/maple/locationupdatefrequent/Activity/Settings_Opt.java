package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Adapters.CheckinAdapter;
import com.example.maple.locationupdatefrequent.Adapters.MessagingAdapter;
import com.example.maple.locationupdatefrequent.Attendance_lo;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Models.Checkins;
import com.example.maple.locationupdatefrequent.Models.DailyReportState;
import com.example.maple.locationupdatefrequent.Models.Messaging;
import com.example.maple.locationupdatefrequent.Models.UploadInstall;
import com.example.maple.locationupdatefrequent.R;
import com.example.maple.locationupdatefrequent.Validations;
import com.example.maple.locationupdatefrequent.rest.ApiClient;
import com.example.maple.locationupdatefrequent.rest.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Settings_Opt extends Activity implements View.OnClickListener {
    CardView sync_cv, logout_cv, download_apk_cv;
    ProgressDialog progress;
    String syncclicked="no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings__opt);
        sync_cv = findViewById(R.id.sync_cv);
        logout_cv = findViewById(R.id.logout_cv);
        download_apk_cv = findViewById(R.id.download_apk_cv);
        sync_cv.setOnClickListener(this);
        logout_cv.setOnClickListener(this);
        download_apk_cv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_cv:
                if (syncclicked.equals("no")){
                     showDialog(Settings_Opt.this,"You need to SYNC to logout. ","no");
                }else {
                    Intent splash = new Intent(Settings_Opt.this, SplashScreen.class);
                    startActivity(splash);
                   // finish();
                }
                break;
            case R.id.sync_cv:
                syncclicked="yes";
                if (getreports_from_local().equals("done")) {
                    System.out.println("wowwwwwwwwww reports called success");
                    if (getcheckins_from_local().equals("done")) {
                        System.out.println("wowwwwwwwwww status called success");
                        if (getmessages_from_local().equals("done")) {
                            showDialog(Settings_Opt.this,"Reports , Messages , Status Syched sucessfully.","yes");
                        }
                    }
                }
                break;
            case R.id.download_apk_cv:
                Intent Downloadapk = new Intent(Settings_Opt.this, Downloadapk.class);
                startActivity(Downloadapk);
                break;
        }
    }

    public void showDialog(Activity activity, String msg, final String status) {
        final Dialog dialog = new Dialog(activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        ImageView b = dialog.findViewById(R.id.b);
        if (status.equals("yes")) {
             b.setVisibility(View.VISIBLE);
        } else {
            b.setVisibility(View.GONE);
        }
        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    public String getreports_from_local() {
        progress = new ProgressDialog(this);
        progress.setMessage("Reports Sending offline data to server..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        Log.d("displaycount reports", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM dailyreports WHERE status='local' ORDER BY rep_date DESC", null);
        Log.d("overallstring", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        Log.d("displaycount reports", ccc);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {

                    if (Validations.hasActiveInternetConnection(Settings_Opt.this)) {
                        if (c.getString(c.getColumnIndex("status")).equals("local")) {
                            sendtoserver(c.getString(c.getColumnIndex("msg")), c.getString(c.getColumnIndex("imagepath")),
                                    c.getString(c.getColumnIndex("latitude")), c.getString(c.getColumnIndex("longitude")),
                                    c.getString(c.getColumnIndex("rep_date")));
                        }
                    }
                    c.moveToNext();
                }
            }
            progress.dismiss();
        } else {
            System.out.println("daily reports progress dismissed");
            progress.dismiss();
        }
        db.close();

        //finish();
        return "done";
    }

    private String getStringImage(String path) {
        String encodedImage = null;
        try {

            if (path != null) {
                Bitmap mBitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos); //bm is the bitmap object
                byte[] byteArrayImage = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            }
        } catch (Exception e) {
            Log.d("unable to read image", e.toString());
//            Toast.makeText(MainActivity1.this,"Retake picture",Toast.LENGTH_SHORT).show();
        }
        return encodedImage;
    }

    public void sendtoserver(final String MessageDescription, final String imagepath, final String Lat, final String Long, final String cdt) {
        if (progress != null && progress.isShowing()) {
            System.out.println("786dadi  reports showing progress");
        } else {
            System.out.println("786dadi  reports created new progress");
            progress = new ProgressDialog(this);
            progress.setMessage("Reports Sending offine data to server..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }

        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);

        ApiInterface apiService = ApiClient.getSams().create(ApiInterface.class);

        retrofit2.Call<UploadInstall> call = apiService.sendMessage(getResources().getString(R.string.version), "VVD@14", s.getString("DeviceId", ""), MessageDescription, Long, Lat,
                s.getString("personname", ""), cdt, "1", s.getString("MobileDeviceID", ""),
                getStringImage(imagepath));
        call.enqueue(new retrofit2.Callback<UploadInstall>() {
            @Override
            public void onResponse(retrofit2.Call<UploadInstall> call, retrofit2.Response<UploadInstall> response) {
                // Log.d("response fromserver" + response.isSuccessful(), String.valueOf(response.body().getMessage_data()));
                if (response.isSuccessful()) {

                    List<DailyReportState> cd = response.body().getMessage_data();
                    if (cd.get(0).getResponse().equals("Fail")) {
                        progress.dismiss();
                        System.out.println("Failureeeeeeeee");
                    } else {
                        progress.dismiss();
                        SQLiteDatabase db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
                        String strSQL = "UPDATE dailyreports SET status = 'online' WHERE cdt = '" + cdt + "'";
                        db.execSQL(strSQL);

                        System.out.println("sucessssssssssssss");
                    }
                } else {
                    progress.dismiss();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<UploadInstall> call, Throwable t) {
                Log.d("response error", t.toString());
                progress.dismiss();
            }
        });

    }


    public String getcheckins_from_local() {
        progress = new ProgressDialog(this);
        progress.setMessage("Status Sending offline data to server..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        Log.d("displaycount checkins", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM checktime WHERE status='local'  ORDER BY cdt DESC LIMIT 1000", null);
        Log.d("overallstring st", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        Log.d("displaycount checkings", ccc);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    if (Validations.hasActiveInternetConnection(Settings_Opt.this)) {
                        if (!c.getString(c.getColumnIndex("latitude")).equals("Unable To Fetch")) {
                            if (c.getString(c.getColumnIndex("status")).equals("local")) {
                                sendlatlong_to_server(c.getString(c.getColumnIndex("latitude")), c.getString(c.getColumnIndex("longitude")),
                                        c.getString(c.getColumnIndex("cdt")), c.getString(c.getColumnIndex("run")));
                            }
                        }
                    }
                    c.moveToNext();
                }
            }
            progress.dismiss();
        } else {
            System.out.println("wowwwwwwwwww status called success");
            progress.dismiss();
        }
        db.close();

        return "done";
        //finish();
    }

    public void sendlatlong_to_server(final String latitude, final String longitude, final String datetime, final String run) {
        if (progress != null && progress.isShowing()) {
            System.out.println("786dadi  updatelocation dismissed");
        } else {
            System.out.println("786dadi  updatelocation created new progress");
            progress = new ProgressDialog(this);
            progress.setMessage("Reports Sending offine data to server..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }
        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/UpdateLocation?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
        urlBuilder.addQueryParameter("DeviceID", s.getString("DeviceId", ""));
        // urlBuilder.addQueryParameter("DeviceID", "5");
        urlBuilder.addQueryParameter("Lat", latitude);
        urlBuilder.addQueryParameter("Long", longitude);
        urlBuilder.addQueryParameter("Altitude", "20");
        urlBuilder.addQueryParameter("Speed", "10");
        urlBuilder.addQueryParameter("Course", "android_srinivas");
        urlBuilder.addQueryParameter("Battery", "20");
        urlBuilder.addQueryParameter("Address", "vizag");
        urlBuilder.addQueryParameter("LocationProvider", s.getString("personname", ""));
        urlBuilder.addQueryParameter("UpdatedDateTime", datetime);
        urlBuilder.addQueryParameter("AppStatus", run);
        urlBuilder.addQueryParameter("MobileDeviceID", s.getString("deviceno", ""));
        //  urlBuilder.addQueryParameter("MobileDeviceID", "9999999999");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("appversion", Settings_Opt.this.getResources().getString(R.string.version).toString())
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progress.dismiss();
                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                Log.d("result", "service no runnning...............");
            }

            @Override
            public void onResponse(Call call, final Response response) {
                SQLiteDatabase db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
                if (!response.isSuccessful()) {
                    progress.dismiss();
                    Log.d("result", response.toString());
                    Log.d("addresss ", getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""));
                    //throw new IOException("Unexpected code " + response);


                    String strSQL = "UPDATE checktime SET status = 'local' WHERE cdt = '" + datetime + "'";
                    db.execSQL(strSQL);


                } else {
                    progress.dismiss();
                    Log.d("result_else_local", response.toString());
                    String strSQL = "UPDATE checktime SET status = 'server' WHERE cdt = '" + datetime + "'";
                    db.execSQL(strSQL);

                }

            }
        });
    }

    private String getaddressFromGEO(double latitude, double longitude) {

        return "wow";
    }


    public String getmessages_from_local() {
        progress = new ProgressDialog(this);
        progress.setMessage("Status Sending offline data to server..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();


        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        Log.d("displaycount messages", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM messages WHERE status='local' ORDER BY cdt DESC", null);
        Log.d("overallstring messages", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        Log.d("displaycount messages", ccc);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    if (Validations.hasActiveInternetConnection(Settings_Opt.this)) {
                        Log.d("mesage", c.getString(c.getColumnIndex("msg")));
                        Log.d("localdb", "internet connection");
                        if (c.getString(c.getColumnIndex("status")).equals("local")) {
                            Log.d("mystatus_local", "Locall status");
                            sendMessage(c.getString(c.getColumnIndex("msg")), c.getString(c.getColumnIndex("latitude")), c.getString(c.getColumnIndex("longitude")),
                                    c.getString(c.getColumnIndex("cdt")), true);
                        }
                    }
                    c.moveToNext();
                }
            }
            progress.dismiss();

        } else {
            progress.dismiss();
        }
        db.close();

        return "done";
        //finish();
    }

    public void sendMessage(final String message, final String latitude, final String longitude, final String datetime, final Boolean mobile) {
        if (progress != null && progress.isShowing()) {
            System.out.println("786dadi  sendmessage dismissed");
        } else {
            System.out.println("786dadi  sendmessages created new progress");
            progress = new ProgressDialog(this);
            progress.setMessage("Reports Sending offine data to server..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }
        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/SendMessage?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
        urlBuilder.addQueryParameter("DeviceID", s.getString("DeviceId", ""));
        urlBuilder.addQueryParameter("MessageDescription", message);
        urlBuilder.addQueryParameter("Long", longitude);
        urlBuilder.addQueryParameter("Lat", latitude);
        urlBuilder.addQueryParameter("ReportedFrom", s.getString("PersonName", ""));
        urlBuilder.addQueryParameter("ReportedDateTime", datetime);
        urlBuilder.addQueryParameter("DR", "0");
        urlBuilder.addQueryParameter("MobileDeviceID", s.getString("MobileDeviceID", ""));
        urlBuilder.addQueryParameter("Photo", "Photo");


        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("appversion", getResources().getString(R.string.version).toString())
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                progress.dismiss();
            }

            @Override
            public void onResponse(Call call, final Response response) {
                progress.dismiss();
                if (!response.isSuccessful()) {
                    Log.d("result", response.toString());
                } else {
                    SQLiteDatabase db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
                    String strSQL = "UPDATE messages SET status = 'server' WHERE cdt = '" + datetime + "'";
                    db.execSQL(strSQL);
                }
            }
        });
    }


}
