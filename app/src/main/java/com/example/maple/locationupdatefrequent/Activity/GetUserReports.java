package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Adapters.AdminMessagingAdapter;
import com.example.maple.locationupdatefrequent.Adapters.GetUserReportsAdapter;
import com.example.maple.locationupdatefrequent.GPSTracker;
import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Reports;
import com.example.maple.locationupdatefrequent.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserReports extends Activity implements View.OnClickListener {
    TextView counter_tv;
    RecyclerView messages_recyler;
    ImageView msg_back_img, send_img, clear_img;
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

        msg_back_img = (ImageView) findViewById(R.id.msg_back_img);
        msg_back_img.setOnClickListener(this);
        messages_recyler = (RecyclerView) findViewById(R.id.reportsmessages_recyler);
        messages_recyler.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
        try {
            progress = new ProgressDialog(this);
            progress.setMessage("Fetching Admin Messages..");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
            getAdminmessages(s.getString("DeviceId", "").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_back_img:
                finish();
                break;
        }
    }

    public void getAdminmessages(String deviceid) throws IOException {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/GetUserReport?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
        urlBuilder.addQueryParameter("DeviceNO", "7702780044");
        urlBuilder.addQueryParameter("DeviceID", "2");
        urlBuilder.addQueryParameter("MobileDeviceID", "4f92900a52d28ab8");

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

                                reports.add(new Reports(values.getString("messagedescription"), values.getString("Photo"),
                                        values.getString("messagedatetime")));
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


}
