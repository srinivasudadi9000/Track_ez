package com.example.maple.locationupdatefrequent.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maple.locationupdatefrequent.Adapters.CheckinAdapter;
import com.example.maple.locationupdatefrequent.Adapters.MessagingAdapter;
import com.example.maple.locationupdatefrequent.Attendance_lo;
import com.example.maple.locationupdatefrequent.GPSTracker;
import com.example.maple.locationupdatefrequent.GeoFencingDemo;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Models.Checkins;
import com.example.maple.locationupdatefrequent.Models.Messaging;
import com.example.maple.locationupdatefrequent.R;
import com.example.maple.locationupdatefrequent.Validations;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

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

public class Messages extends Activity implements View.OnClickListener {
    TextView counter_tv;
    RecyclerView messages_recyler;
    ImageView msg_back_img, send_img, clear_img;
    EditText et_content;
    GPSTracker gps;
    String latitude, longitude;
    ArrayList<Messaging> messages;
    MessagingAdapter messagingAdapter;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        gps = new GPSTracker(this);
        if (!gps.isGPSEnabled && !gps.isNetworkEnabled) {
            Log.d("networkd", "false");
            gps.showSettingsAlert(Messages.this);
        } else {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
            // Toast.makeText(getBaseContext(),latitude+" "+longitude  ,Toast.LENGTH_SHORT).show();
        }

        counter_tv = findViewById(R.id.counter_tv);
        msg_back_img = findViewById(R.id.msg_back_img);
        send_img = findViewById(R.id.send_img);
        clear_img = findViewById(R.id.clear_img);
        messages_recyler = findViewById(R.id.messages_recyler);
        messages_recyler.setLayoutManager(new LinearLayoutManager(this));
        et_content = findViewById(R.id.et_content);
        msg_back_img.setOnClickListener(this);
        send_img.setOnClickListener(this);
        clear_img.setOnClickListener(this);
        getcheckins_from_local();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_back_img:
                finish();
                break;
            case R.id.send_img:
                SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
                Log.d("Deviceid", s.getString("DeviceId", ""));
                Log.d("Mobile DeviceId", s.getString("MobileDeviceID", ""));
                Log.d("Reported From", s.getString("PersonName", ""));
                Log.d("latitude", latitude);
                Log.d("longitude", longitude);

                if (et_content.getText().toString().length() < 15) {
                    showDialog(Messages.this, "Message Should Be Minimum 15 Characters", "no");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String millisInString = dateFormat.format(new Date());
                    if (Validations.hasActiveInternetConnection(Messages.this)) {
                        progress = new ProgressDialog(this);
                        progress.setMessage("Fetching Messages..");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.setCancelable(false);
                        progress.show();
                        sendMessage(et_content.getText().toString(), latitude, longitude, millisInString, false);
                    } else {
                        DBHelper dbHelper = new DBHelper(Messages.this);
                        dbHelper.insertMessage(latitude, longitude, et_content.getText().toString(), millisInString, "local", Messages.this);
                        showDialog(Messages.this, "Message Sending In Progress..", "yes");
                    }
                }

                break;
            case R.id.clear_img:
                Log.d("MOdelnmbrdisplay", android.os.Build.MODEL);
                Log.d("device_build", Build.DEVICE);
                Log.d("manufacturer", android.os.Build.MANUFACTURER);
                Log.d("Devicename", getDeviceName());
                et_content.setText("");
                clear_img.setClickable(false);
                getcheckins_from_local();
                break;
        }
    }

    public static String getDeviceName() {
        String deviceName = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
        return deviceName;
    }

    public void sendMessage(final String message, final String latitude, final String longitude, final String datetime, final Boolean mobile) {
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
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                if (!mobile) {
                    progress.dismiss();
                    Log.d("result", "service no runnning...............");
                    DBHelper dbHelper = new DBHelper(Messages.this);
                    dbHelper.insertMessage(latitude, longitude, et_content.getText().toString(), datetime, "local", Messages.this);
                    showDialog(Messages.this, "Message Sending In Progress..", "yes");
                }
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (!response.isSuccessful()) {
                    Log.d("result", response.toString());
                    if (!mobile) {
                        progress.dismiss();
                        DBHelper dbHelper = new DBHelper(Messages.this);
                        dbHelper.insertMessage(latitude, longitude, et_content.getText().toString(), datetime, "local", Messages.this);
                    } else {
                        showDialog(Messages.this, "Message Sending In Progress..", "yes");
                    }
                } else {
                    if (mobile) {
                        DBHelper dbHelper = new DBHelper();
                        dbHelper.updateMessage("server", datetime, Messages.this);
                    } else {
                        progress.dismiss();
                        Log.d("result_else", response.toString());
                        SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
                        DBHelper dbHelper = new DBHelper(Messages.this);
                        dbHelper.insertMessage(latitude, longitude, et_content.getText().toString(), datetime, "server", Messages.this);
                        Messages.this.runOnUiThread(new Runnable() {
                            public void run() {
                                showDialog(Messages.this, "Message Sent Successfully", "yes");
                            }
                        });
                    }

                }
            }
        });
    }

    public void getcheckins_from_local() {

        messages = new ArrayList<Messaging>();
        messages.clear();
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        Log.d("displaycount", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT * FROM messages ORDER BY cdt DESC", null);
        Log.d("overallstring", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        Log.d("displaycount", ccc);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                messages.add(new Messaging(c.getString(c.getColumnIndex("msg")), c.getString(c.getColumnIndex("status")),
                        c.getString(c.getColumnIndex("cdt"))));
                if (Validations.hasActiveInternetConnection(Messages.this)) {
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
        db.close();

        messagingAdapter = new MessagingAdapter(messages, R.layout.message_single, getApplicationContext());
        messages_recyler.setAdapter(messagingAdapter);
        messagingAdapter.notifyDataSetChanged();
        clear_img.setClickable(true);
        //finish();
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
            et_content.setText("");
            b.setVisibility(View.VISIBLE);
        } else {
            b.setVisibility(View.GONE);
        }
        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getcheckins_from_local();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void removeListItem(View rowView, final int position) {

        Animation anim = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        anim.setDuration(500);
        rowView.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {

            public void run() {

                messages.remove(position); //Remove the current content from the array

                messagingAdapter.notifyDataSetChanged(); //Refresh list
            }

        }, anim.getDuration());
    }

}
