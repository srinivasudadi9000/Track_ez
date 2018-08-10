package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Adapters.AdminMessagingAdapter;
import com.example.maple.locationupdatefrequent.Adapters.MessagingAdapter;
import com.example.maple.locationupdatefrequent.GPSTracker;
import com.example.maple.locationupdatefrequent.GeoFencingDemo;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Messaging;
import com.example.maple.locationupdatefrequent.R;
import com.example.maple.locationupdatefrequent.Validations;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminMessages extends Activity implements View.OnClickListener {
    TextView counter_tv;
    RecyclerView messages_recyler;
    ImageView msg_back_img, send_img, clear_img;
    EditText et_content;
    GPSTracker gps;
    String latitude, longitude;
    ArrayList<Admin> admins;
    AdminMessagingAdapter adminMessagingAdapter;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminmessages);
        admins = new ArrayList<Admin>();
        gps = new GPSTracker(this);
        if (!gps.isGPSEnabled && !gps.isNetworkEnabled) {
            Log.d("networkd", "false");
            gps.showSettingsAlert(AdminMessages.this);
        } else {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
            // Toast.makeText(getBaseContext(),latitude+" "+longitude  ,Toast.LENGTH_SHORT).show();
        }

        msg_back_img = findViewById(R.id.msg_back_img);
        msg_back_img.setOnClickListener(this);
        messages_recyler = findViewById(R.id.adminmessages_recyler);
        messages_recyler.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
        progress = new ProgressDialog(this);
        progress.setMessage("Fetching Admin Messages..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
        getAdminmessages(s.getString("DeviceId", "").toString());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_back_img:
                finish();
                break;
        }
    }

    public void getAdminmessages(String deviceid) {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/GetAdminMessages?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
        urlBuilder.addQueryParameter("DeviceID", deviceid);

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
                showlocally();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progress.dismiss();
                if (response.isSuccessful()) {
                    Log.d("result_success", response.body().toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        SharedPreferences.Editor s = getSharedPreferences("AdminMessages", MODE_PRIVATE).edit();
                        s.putString("status", "yes");
                        s.putString("object", jsonObject.toString());
                        s.commit();
                        showlocally();
                    } catch (JSONException e) {
                        showlocally();
                        e.printStackTrace();
                    }
                    //  throw new IOException("Unexpected code " + response.body().toString());
                } else {
                    showlocally();
                    Log.d("result_else", response.body().toString());
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                }
            }
        });
    }

    public void showlocally() {

        try {
            SharedPreferences s = getSharedPreferences("AdminMessages", MODE_PRIVATE);
            if (s.getString("status", "").equals("yes")) {

                JSONObject jsonObject = new JSONObject(s.getString("object", ""));
                if (jsonObject.getString("Response").equals("Success")) {
                    if (jsonObject.get("Message") instanceof JSONArray) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Message");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);
                                Log.d("hello", values.getString("Message"));
                                Log.d("hello", values.getString("MessageDateTime"));

                                admins.add(new Admin(values.getString("Message"), values.getInt("MessageID"),
                                        values.getString("MessageDateTime"), values.getInt("ReadStatus")));
                            }
                            AdminMessages.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    adminMessagingAdapter = new AdminMessagingAdapter(admins, R.layout.adminmessage_single, getApplicationContext());
                                    messages_recyler.setAdapter(adminMessagingAdapter);
                                    adminMessagingAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            AdminMessages.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showDialog(AdminMessages.this, "No messages to display", "no");
                                }
                            });
                        }

                    } else {
                        String jsobje = jsonObject.getString("Message");
                        Log.d("elesdddd", jsobje);
                    }
                }
            } else {
                AdminMessages.this.runOnUiThread(new Runnable() {
                    public void run() {
                        showDialog(AdminMessages.this, "No messages to display", "no");
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
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
                if (status.equals("yes")) {
                    dialog.dismiss();

                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

}
