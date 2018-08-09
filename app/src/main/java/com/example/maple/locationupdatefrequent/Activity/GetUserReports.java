package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Adapters.AdminMessagingAdapter;
import com.example.maple.locationupdatefrequent.Adapters.CheckinAdapter;
import com.example.maple.locationupdatefrequent.Adapters.GetUserReportsAdapter;
import com.example.maple.locationupdatefrequent.Attendance_lo;
import com.example.maple.locationupdatefrequent.GPSTracker;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Checkins;
import com.example.maple.locationupdatefrequent.Models.DailyReportState;
import com.example.maple.locationupdatefrequent.Models.Reports;
import com.example.maple.locationupdatefrequent.Models.UploadInstall;
import com.example.maple.locationupdatefrequent.R;
import com.example.maple.locationupdatefrequent.Validations;
import com.example.maple.locationupdatefrequent.rest.ApiClient;
import com.example.maple.locationupdatefrequent.rest.ApiInterface;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class GetUserReports extends Activity implements View.OnClickListener {
    TextView counter_tv;
    RecyclerView messages_recyler;
    ImageView msg_back_img, refresh_img_user, clear_img;
    EditText et_content;
    GPSTracker gps;
    String latitude, longitude;
    ArrayList<Reports> reports;
    GetUserReportsAdapter getUserReportsAdapter;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userreports);
        reports = new ArrayList<Reports>();
        gps = new GPSTracker(this);
        if (!gps.isGPSEnabled && !gps.isNetworkEnabled) {
            Log.d("networkd", "false");
            gps.showSettingsAlert(GetUserReports.this);
        } else {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
            // Toast.makeText(getBaseContext(),latitude+" "+longitude  ,Toast.LENGTH_SHORT).show();
        }
        refresh_img_user = findViewById(R.id.refresh_img_user);
        refresh_img_user.setOnClickListener(this);

        msg_back_img = findViewById(R.id.msg_back_img);
        msg_back_img.setOnClickListener(this);
        messages_recyler = findViewById(R.id.reportsmessages_recyler);
        messages_recyler.setLayoutManager(new LinearLayoutManager(this));

        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM dailyreports WHERE status='local'", null);
        if (c.getCount() > 0) {
            refresh_img_user.setVisibility(View.VISIBLE);
        }
        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);

      /*  if (Validations.hasActiveInternetConnection(GetUserReports.this)) {
            progress = new ProgressDialog(this);
            progress.setMessage("Fetching Admin Messages..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
            getUserReports(s.getString("DeviceId", "").toString());
        } else {

            getreports_from_local();
        }*/
        getreports_from_local();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_back_img:
                finish();
                break;
            case R.id.refresh_img_user:

                Intent getuser = new Intent(GetUserReports.this,GetUserReports.class);
                startActivity(getuser);
                finish();
                break;
        }
    }

    public void getUserReports(String deviceid) {
        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/GetUserReport?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
       /* urlBuilder.addQueryParameter("DeviceNO", "9393111282");
        urlBuilder.addQueryParameter("DeviceID", "2");
        urlBuilder.addQueryParameter("MobileDeviceID", "4f92900a52d28ab8");*/
        urlBuilder.addQueryParameter("DeviceNO", s.getString("deviceno", ""));
        urlBuilder.addQueryParameter("DeviceID", s.getString("DeviceId", ""));
        urlBuilder.addQueryParameter("MobileDeviceID", s.getString("MobileDeviceID", ""));

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("myrequest", request.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                Log.d("result", "service no runnning...............");
                progress.dismiss();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progress.dismiss();
                if (response.isSuccessful()) {
                    Log.d("result_success", response.body().toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.get("Message") instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Message");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);
                                Log.d("hello", values.getString("messagedescription"));
                                Log.d("hello", values.getString("messagedatetime"));
                                SQLiteDatabase db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);
                                Cursor c = db.rawQuery("SELECT * FROM dailyreports WHERE rep_date='" + values.getString("messagedatetime") + "'", null);
                                if (c.moveToFirst()) {
                                    Log.d("UPdate", "updating......");
                                    String strSQL = "UPDATE dailyreports SET msg = '" + values.getString("messagedescription") + "',rep_date='" +
                                            values.getString("messagedatetime") + "',imagepath='" + values.getString("Photo") +
                                            "' WHERE rep_date = '" + values.getString("messagedatetime") + "'";
                                    db.execSQL(strSQL);
                                } else {
                                    DBHelper dbHelper = new DBHelper();
                                    dbHelper.insertReport("", "", values.getString("messagedescription"), "",
                                            values.getString("messagedatetime"), values.getString("Photo"), "online", GetUserReports.this);
                                    Log.d("INserting", "INserting......");

                                }
                                reports.add(new Reports(values.getString("messagedescription"), values.getString("Photo"),
                                        values.getString("messagedatetime"), ""));
                            }
                            GetUserReports.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    getUserReportsAdapter = new GetUserReportsAdapter(reports, R.layout.userreports_single, getApplicationContext());
                                    messages_recyler.setAdapter(getUserReportsAdapter);
                                    getUserReportsAdapter.notifyDataSetChanged();
                                }
                            });

                        } else {
                            String jsobje = jsonObject.getString("Message");
                            Log.d("elesdddd", jsobje);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  throw new IOException("Unexpected code " + response.body().toString());
                } else {
                    Log.d("result_else", response.body().toString());
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                }
            }
        });
    }

    public void getreports_from_local() {
        progress = new ProgressDialog(this);
        progress.setMessage("Sending offline data to server..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        Log.d("displaycount", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM dailyreports ORDER BY rep_date DESC", null);
        Log.d("overallstring", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        Log.d("displaycount", ccc);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                if (Validations.hasActiveInternetConnection(GetUserReports.this)) {
                    if (c.getString(c.getColumnIndex("status")).equals("local")) {
                        sendtoserver(c.getString(c.getColumnIndex("msg")), c.getString(c.getColumnIndex("imagepath")),
                                c.getString(c.getColumnIndex("latitude")), c.getString(c.getColumnIndex("longitude")),
                                c.getString(c.getColumnIndex("rep_date")));
                    }
                } else {
                    reports.add(new Reports(c.getString(c.getColumnIndex("msg")), c.getString(c.getColumnIndex("imagepath")),
                            c.getString(c.getColumnIndex("rep_date")), c.getString(c.getColumnIndex("status"))));
                }
                c.moveToNext();
            }
        }
        db.close();
        if (Validations.hasActiveInternetConnection(GetUserReports.this)) {
            SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
            if (progress != null && progress.isShowing()) {

            } else {
                progress = new ProgressDialog(this);
                progress.setMessage("Fetching Admin Messages..");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setCancelable(false);
                progress.show();
            }
            getUserReports(s.getString("DeviceId", "").toString());

        } else {
            getUserReportsAdapter = new GetUserReportsAdapter(reports, R.layout.userreports_single, getApplicationContext());
            messages_recyler.setAdapter(getUserReportsAdapter);
            getUserReportsAdapter.notifyDataSetChanged();
            progress.dismiss();
        }
        //finish();
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

        } else {
            progress = new ProgressDialog(this);
            progress.setMessage("Sending offine data to server..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }

        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);

        ApiInterface apiService = ApiClient.getSams().create(ApiInterface.class);

        retrofit2.Call<UploadInstall> call = apiService.sendMessage("VVD@14", s.getString("DeviceId", ""), MessageDescription, Long, Lat,
                s.getString("personname", ""), cdt, "1", s.getString("MobileDeviceID", ""),
                getStringImage(imagepath));
        call.enqueue(new retrofit2.Callback<UploadInstall>() {
            @Override
            public void onResponse(retrofit2.Call<UploadInstall> call, retrofit2.Response<UploadInstall> response) {

                // Log.d("response fromserver" + response.isSuccessful(), String.valueOf(response.body().getMessage_data()));
                if (response.isSuccessful()) {
                    progress.dismiss();
                    List<DailyReportState> cd = response.body().getMessage_data();
                    if (cd.get(0).getResponse().equals("Fail")) {
                        System.out.println("Failureeeeeeeee");
                    } else {
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


}
