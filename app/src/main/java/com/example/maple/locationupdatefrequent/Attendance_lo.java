package com.example.maple.locationupdatefrequent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Adapters.CheckinAdapter;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Models.Checkins;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Attendance_lo extends Activity implements View.OnClickListener{
    ImageView clickback;
    TextView title_tv;
    RecyclerView check_today_rv;
    ArrayList<Checkins> checkins;
    CheckinAdapter checkinAdapter;
    ImageView refresh_img,back_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_lo);

        refresh_img = (ImageView) findViewById(R.id.refresh_img);
        back_img = (ImageView) findViewById(R.id.back_img);
        title_tv = (TextView) findViewById(R.id.title_tv);
        check_today_rv = (RecyclerView) findViewById(R.id.check_today_rv);
        check_today_rv.setLayoutManager(new LinearLayoutManager(this));
        getcheckins_from_local();
        refresh_img.setOnClickListener(this);
        back_img.setOnClickListener(this);
        title_tv.setText("status");
    }

    public void getcheckins_from_local() {

        checkins = new ArrayList<Checkins>();
        checkins.clear();
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        Log.d("displaycount", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM checktime ORDER BY cdt DESC", null);
        Log.d("overallstring", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        Log.d("displaycount", ccc);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                checkins.add(new Checkins(c.getString(c.getColumnIndex("latitude")), c.getString(c.getColumnIndex("longitude")),
                        c.getString(c.getColumnIndex("cdt")), c.getString(c.getColumnIndex("address")), c.getString(c.getColumnIndex("deviceid"))
                        , c.getString(c.getColumnIndex("deviceno")), c.getString(c.getColumnIndex("status"))));
                try {
                    if (Validations.hasActiveInternetConnection(Attendance_lo.this)) {
                        if (!c.getString(c.getColumnIndex("latitude")).equals("Unable To Fetch")){
                            if ( c.getString(c.getColumnIndex("status")).equals("local")){
                                sendlatlong_to_server(c.getString(c.getColumnIndex("latitude")), c.getString(c.getColumnIndex("longitude")),
                                        c.getString(c.getColumnIndex("cdt")));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                c.moveToNext();
            }
        }
        db.close();

        checkinAdapter = new CheckinAdapter(checkins, R.layout.check_single, getApplicationContext());
        check_today_rv.setAdapter(checkinAdapter);
        checkinAdapter.notifyDataSetChanged();
        //finish();
    }

    public void sendlatlong_to_server(final String latitude, final String longitude, final String datetime) throws IOException {
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
        urlBuilder.addQueryParameter("LocationProvider", s.getString("personname",""));
        urlBuilder.addQueryParameter("UpdatedDateTime", datetime);
        urlBuilder.addQueryParameter("AppStatus", "2");
        urlBuilder.addQueryParameter("MobileDeviceID",  s.getString("deviceno", ""));
        //  urlBuilder.addQueryParameter("MobileDeviceID", "9999999999");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                Log.d("result", "service no runnning...............");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("result", response.toString());
                    Log.d("addresss ", getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""));
                    //throw new IOException("Unexpected code " + response);

                    DBHelper dbHelper = new DBHelper(Attendance_lo.this);
                    dbHelper.updateProject("local", datetime, Attendance_lo.this);
                } else {
                    Log.d("result_else_local", response.toString());
                    SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
                    DBHelper dbHelper = new DBHelper(Attendance_lo.this);
                    dbHelper.updateProject("server", datetime, Attendance_lo.this);
                }
            }
        });
    }

    private String getaddressFromGEO(double latitude, double longitude) {

        return "wow";
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_img:
                finish();
                break;
            case R.id.refresh_img:
                getcheckins_from_local();
                break;
        }
    }
}
