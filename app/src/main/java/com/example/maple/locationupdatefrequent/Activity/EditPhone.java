package com.example.maple.locationupdatefrequent.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maple.locationupdatefrequent.GeoFencingDemo;
import com.example.maple.locationupdatefrequent.Helper.Typewriter;
import com.example.maple.locationupdatefrequent.R;
import com.example.maple.locationupdatefrequent.Validations;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditPhone extends Activity implements View.OnClickListener {
    LinearLayout view_mobile_edt_ll, view_mobile_ll;
    TextView edit_tv;
    AppCompatButton okay_app_compact, register_app;
    RadioButton phone_no_radio;
    EditText phon_no_edtxt;
    LinearLayout close_img;
    Boolean selected = false;
    ProgressDialog progress;

    //Typewriter mobile_tv;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_phone);
        // mobile_tv = (Typewriter) findViewById(R.id.mobile_tv);
        view_mobile_ll = findViewById(R.id.view_mobile_ll);
        view_mobile_edt_ll = findViewById(R.id.view_mobile_edt_ll);
        edit_tv = findViewById(R.id.edit_tv);
        phone_no_radio = findViewById(R.id.phone_no_radio);

    /*    mobile_tv.setCharacterDelay(150);
        mobile_tv.animateText("Mobile Number");*/

        close_img = findViewById(R.id.cancel_ll);
        okay_app_compact = findViewById(R.id.okay_app_compact);
        phon_no_edtxt = findViewById(R.id.phon_no_edtxt);
        // 1st screen
        register_app = findViewById(R.id.register_app);
        okay_app_compact.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        register_app.setOnClickListener(this);
        close_img.setOnClickListener(this);
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        String mPhoneNumber = tMgr.getLine1Number();
        if (mPhoneNumber.equals("")) {
            mPhoneNumber = "0000000000";
            phone_no_radio.setText(mPhoneNumber);
        } else {
            phone_no_radio.setText(mPhoneNumber);
            edit_tv.setFocusable(true);
        }

        phone_no_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selected = true;
                // Toast.makeText(getBaseContext(), compoundButton.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("editphone", mPhoneNumber);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidein);
        view_mobile_ll.startAnimation(animation);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okay_app_compact:
                if (phon_no_edtxt.getText().toString().length() == 0) {
                    showDialog(EditPhone.this, "Please enter valid 10 digit registered mobile number", "no");
                } else if (phon_no_edtxt.getText().toString().length() < 10) {
                    showDialog(EditPhone.this, "Please enter valid 10 digit registered mobile number", "no");
                } else {

                    String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.d("deviceid", id);
                    Log.d("device name", android.os.Build.MODEL);
                    Log.d("device_build", Build.DEVICE);
                    Log.d("manufacturer", android.os.Build.MANUFACTURER);
                    if (Validations.hasActiveInternetConnection(EditPhone.this)) {
                        progress = new ProgressDialog(this);
                        progress.setMessage("Authenticating User..");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.setCancelable(false);
                        progress.show();
                        setRegisterdetails(phon_no_edtxt.getText().toString());
                        //getuserdetails(phon_no_edtxt.getText().toString());
                    } else {
                        showDialog(EditPhone.this, "Your device does not seem to be connected to internet, enable and retry", "no");
                    }

                }
                break;
            case R.id.edit_tv:

                view_mobile_ll.setVisibility(View.GONE);
                view_mobile_edt_ll.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidein);
                view_mobile_edt_ll.startAnimation(animation);
                /*mobile_tv.setCharacterDelay(150);
                mobile_tv.animateText("Mobile Number");*/
                break;

            case R.id.register_app:

                if (selected) {
                    if (Validations.hasActiveInternetConnection(EditPhone.this)) {
                        progress = new ProgressDialog(this);
                        progress.setMessage("Authenticating User..");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.setCancelable(false);
                        progress.show();
                        //  Log.d("phonenuo........", phone_no_radio.getText().subSequence(2, 12).toString());
                        if (phone_no_radio.getText().toString().length() < 10) {
                            //getuserdetails(phone_no_radio.getText().toString());
                            setRegisterdetails(phone_no_radio.getText().toString());
                        } else {
                            if (phone_no_radio.getText().length() == 10) {
                                setRegisterdetails(phone_no_radio.getText().subSequence(0, 10).toString());
                            } else if (phone_no_radio.getText().length() == 11) {
                                setRegisterdetails(phone_no_radio.getText().subSequence(1, 11).toString());
                            } else if (phone_no_radio.getText().length() == 12) {
                                setRegisterdetails(phone_no_radio.getText().subSequence(2, 12).toString());
                            }
                            // getuserdetails(phone_no_radio.getText().subSequence(2, 12).toString());
                        }
                    } else {
                        showDialog(EditPhone.this, "Your device does not seem to be connected to internet, enable and retry", "no");
                    }
                    // showDialog(EditPhone.this, "Successfully ", "yes");
                } else {
                    showDialog(EditPhone.this, "Please select phone number (or) Edit to enter your registered number", "no");
                }
                break;
            case R.id.cancel_ll:
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
                view_mobile_edt_ll.startAnimation(animation2);
                view_mobile_ll.setVisibility(View.VISIBLE);
                view_mobile_edt_ll.setVisibility(View.GONE);
                phon_no_edtxt.setText("");
              /*  mobile_tv.setCharacterDelay(150);
                mobile_tv.animateText("Mobile Number");*/
                // Toast.makeText(getBaseContext(), "Exit Button", Toast.LENGTH_SHORT).show();
                //finish();
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
                if (status.equals("yes")) {
                    dialog.dismiss();
                    Intent dashboard = new Intent(EditPhone.this, GeoFencingDemo.class);
                    startActivity(dashboard);
                    finish();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    public static String getDeviceName() {
        String deviceName = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
        return deviceName;
    }

    public void setRegisterdetails(String DeviceNo) {
        final String MobileDeviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String Device_name = android.os.Build.MODEL;
        Log.d("deviceid", MobileDeviceID);
        Log.d("device name", getDeviceName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String millisInString = dateFormat.format(new Date());

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/RegisterDevice?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
        urlBuilder.addQueryParameter("DeviceNo", DeviceNo);
        urlBuilder.addQueryParameter("MobileDeviceID", MobileDeviceID);
        urlBuilder.addQueryParameter("RegistrationDateTime", millisInString);
        urlBuilder.addQueryParameter("DeviceName", Device_name);

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("RegisterDevice", request.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progress.dismiss();
                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                Log.d("result", "service no runnning...............");
                showDialog(EditPhone.this, "Internal server occured please try again", "no");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progress.dismiss();
                if (response.isSuccessful()) {
                    Log.d("result_success", response.body().toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        JSONArray jsonArray = jsonObject.getJSONArray("Message");
                        Log.d("success", jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject values = jsonArray.getJSONObject(i);
                            if (values.getString("Response").equals("Success")) {
                                SharedPreferences.Editor s = getSharedPreferences("Userdetails", MODE_PRIVATE).edit();
                                s.putString("DeviceId", values.getString("DeviceID"));
                                s.putString("deviceno", values.getString("DeviceNo"));
                                s.putString("MobileDeviceID", MobileDeviceID);
                                s.putString("start_stop", "");
                                s.putString("personname", values.getString("PersonName"));
                                s.putString("CategoryID", values.getString("CategoryID"));
                                s.putString("category", values.getString("Category"));
                                s.putString("RefreshTimeInterval", values.getString("RefreshTimeInterval"));
                                s.commit();
                                JsonParser jsonParser = new JsonParser();
                                //     JsonArray arrayFromString = jsonParser.parse(String.valueOf(values.getJSONArray("Centers"))).getAsJsonArray();
                                // JSONArray jsonArray2 = values.getJSONArray("Centers");
                                // Convert JSON Array String into JSON Array
                                String jsonArrayString = values.getString("Centers").toString();
                                JsonArray arrayFromString = jsonParser.parse(jsonArrayString).getAsJsonArray();
                                System.out.println(arrayFromString.toString());
                                JSONArray jsonObject1 = new JSONArray(arrayFromString.toString());
                                for (int j = 0; j < jsonObject1.length(); j++) {
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject(j);
                                    System.out.println("Good DADTi................." + jsonObject2.getString("CenterName"));
                                }
                                System.out.println("Jsonobject1 " + arrayFromString.toString());
                                String ReportParameters = values.getString("ReportParameters").toString();
                                JsonArray ReportParametersa = jsonParser.parse(ReportParameters).getAsJsonArray();
                                System.out.println(ReportParametersa.toString());

                                SharedPreferences.Editor editor = getSharedPreferences("questions", MODE_PRIVATE).edit();
                                editor.putString("Centers", arrayFromString.toString());
                                editor.putString("ReportParameters", ReportParametersa.toString());
                                editor.commit();
                                EditPhone.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showDialog(EditPhone.this, "Registration successfull, click OK to continue", "yes");
                                    }
                                });

                            } else {
                                EditPhone.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            showDialog(EditPhone.this, values.getString("Message"), "no");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  throw new IOException("Unexpected code " + response.body().toString());
                } else {
                    Log.d("result_else", response.body().toString());
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                    showDialog(EditPhone.this, "Server busy at this moment please try after some time or contact admin", "no");
                }
            }
        });
    }


    public void getuserdetails(String phonnumber) {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://125.62.194.181/tracker/trackernew.asmx/GetUserDetails?").newBuilder();
        urlBuilder.addQueryParameter("Token", "VVD@14");
        urlBuilder.addQueryParameter("DeviceNO", phonnumber);

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

                if (response.isSuccessful()) {
                    Log.d("result_success", response.body().toString());
                    progress.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.get("Message") instanceof JSONArray) {
                            String deviceno = null;
                            String MobileDeviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                            JSONArray jsonArray = jsonObject.getJSONArray("Message");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);
                                deviceno = values.getString("DeviceNo");
                                Log.d("hello", values.getString("DeviceID"));
                                Log.d("hello", values.getString("DeviceNo"));
                                Log.d("hello", values.getString("PersonName"));
                                Log.d("hello", values.getString("CategoryID"));
                                Log.d("hello", values.getString("Category"));
                                Log.d("hello", values.getString("RefreshTimeInterval"));
                                SharedPreferences.Editor s = getSharedPreferences("Userdetails", MODE_PRIVATE).edit();
                                s.putString("DeviceId", values.getString("DeviceID"));
                                s.putString("deviceno", values.getString("DeviceNo"));
                                s.putString("MobileDeviceID", MobileDeviceID);
                                s.putString("personname", values.getString("PersonName"));
                                s.putString("CategoryID", values.getString("CategoryID"));
                                s.putString("category", values.getString("Category"));
                                s.putString("RefreshTimeInterval", values.getString("RefreshTimeInterval"));
                                s.commit();
                            }

                            Log.d("deviceno_calline", deviceno);
                            setRegisterdetails(deviceno);


                        } else {
                            String jsobje = jsonObject.getString("Message");
                            Log.d("elesdddd", jsobje);
                            EditPhone.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showDialog(EditPhone.this, "Phone Number Does Not Exist Please Contact Admin", "no");
                                }
                            });

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
