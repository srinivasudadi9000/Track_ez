/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.maple.locationupdatefrequent;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.maple.locationupdatefrequent.Helper.Constants;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.google.android.gms.location.LocationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * Receiver for handling location updates.
 * <p>
 * For apps targeting API level O
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should not be used.
 * <p>
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 * foreground.
 */
public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";
    Context context;
    static final String ACTION_PROCESS_UPDATES =
            "com.google.android.gms.location.sample.backgroundlocationupdates.action" +
                    ".PROCESS_UPDATES";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent w = new Intent(context, LocationUpdatesBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, w, 0);
        // cal.add(Calendar.SECOND, 5);
        // alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        //alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,cal.getTimeInMillis(), pendingIntent);

        // alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 60000, pendingIntent);
        SharedPreferences ss = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
        int seconds = Integer.parseInt(ss.getString("RefreshTimeInterval", ""));
        int timeinterval = (int) TimeUnit.SECONDS.toMillis(seconds);
        System.out.println("Geo Milliseconds at receiver " + timeinterval);
        if (android.os.Build.MANUFACTURER.equals("LeMobile")) {

        } else if (android.os.Build.MANUFACTURER.equals("vivo")) {
            //alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, pendingIntent);
            alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeinterval, pendingIntent);
            //alarmMgr.set(android.app.AlarmManager.RTC_WAKEUP,30000, pendingIntent);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (android.os.Build.MANUFACTURER.equals("LeMobile")) {

                } else {
                    //   alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, pendingIntent);
                    alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeinterval, pendingIntent);
                }
                // only for gingerbread and newer versions
            }
        }
        Log.d(TAG, "onReceive");
        if (intent != null) {
            Log.d(TAG, "onReceive entered if");
            //  Log.d("addresss ",getaddressFromGEO(17.7167105,83.306409));
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Log.d(TAG, "onReceive entered ACTION_PROCESS_UPDATES");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String millisInString = dateFormat.format(new Date());

                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Log.d(TAG, "onReceive entered result");
                    List<Location> locations = result.getLocations();
                    LocationResultHelper locationResultHelper = new LocationResultHelper(context, locations.get(0));
                    // Save the location data to SharedPreferences.
                    locationResultHelper.saveResults();
                    // Show notification with the location data.
                    //locationResultHelper.showNotification();
//                    Log.i(TAG, LocationResultHelper.getSavedLocationResult(context));

                    String time = LocationResultHelper.getLocationResultTitle();
                    String latitude = String.valueOf(locations.get(0).getLatitude());
                    String longitude = String.valueOf(locations.get(0).getLongitude());


                    SharedPreferences vendorname = context.getSharedPreferences("dump", MODE_PRIVATE);
                    int o_value = vendorname.getInt("value", 0);
                    String xy = String.valueOf(vendorname.getInt("value", 0));
                    Log.d("valueinshared", xy);
                    Log.d(TAG, ", Lat: " + latitude + ", Long: " + longitude + ", Time: " + time);
                   /* DBHelper dbHelper = new DBHelper(context);
                    dbHelper.insertProject(latitude, longitude, millisInString, context);*/
                    if (Validations.hasActiveInternetConnection(context)) {

                        /* if (o_value % 3 == 0) {
                            SharedPreferences.Editor s = context.getSharedPreferences("dump", MODE_PRIVATE).edit();
                            o_value++;
                            s.putInt("value", o_value);
                            s.commit();
                            //  sendlatlong_to_server(latitude, longitude, millisInString);
                            sendlatlong_to_server(latitude, longitude, millisInString);
                        } else {
                            SharedPreferences.Editor s = context.getSharedPreferences("dump", MODE_PRIVATE).edit();
                            o_value++;
                            s.putInt("value", o_value++);
                            s.commit();
                        }*/
                        sendlatlong_to_server(latitude, longitude, millisInString);
                    } else {
                        SharedPreferences s = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
                        DBHelper dbHelper = new DBHelper(context);
                        if (Validations.hasActiveInternetConnection(context)) {
                            dbHelper.insertProject(latitude, longitude, millisInString, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                                    s.getString("DeviceId", ""), s.getString("deviceno", ""), "local", "2", context);
                        } else {
                            dbHelper.insertProject(latitude, longitude, millisInString, "oops",
                                    s.getString("DeviceId", ""), s.getString("deviceno", ""), "local", "2", context);
                        }
                    }

//                    Stock stk = new Stock();
//                    stk.setSymbol(String.valueOf(locations.get(0).getLatitude())
//                            + ", "+ String.valueOf(locations.get(0).getLongitude()));

//                    performSubmit(stk, context);

                    // Creating an intent for broadcastreceiver
                    Intent broadcastIntent = new Intent(Constants.BROADCAST_ACTION);
                    // Attaching data to the intent
                    broadcastIntent.putExtra(Constants.EXTENDED_DATA_STATUS, result);
                    // Sending the broadcast
                    LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
                } else {
                    SharedPreferences s = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
                    GPSTracker gpsTracker = new GPSTracker(context);
                    if (gpsTracker.isGPSEnabled) {
                        if (gpsTracker.canGetLocation()) {
                            String lat, longi;
                            lat = String.valueOf(gpsTracker.getLatitude());
                            longi = String.valueOf(gpsTracker.getLongitude());
                            if (lat.equals("0.0") || longi.equals("0.0")) {

                            } else {
                                DBHelper dbHelper = new DBHelper(context);
                                dbHelper.insertProject(lat, longi, millisInString, "wow",
                                        s.getString("DeviceId", ""), s.getString("deviceno", ""), "local"
                                        , "2", context);
                            }
                        } else {
                            DBHelper dbHelper = new DBHelper(context);
                            dbHelper.insertProject("Unable To Fetch", "Unable To Fetch", millisInString, "wow",
                                    s.getString("DeviceId", ""), s.getString("deviceno", ""), "server"
                                    , "2", context);
                        }
                    } else {
                        DBHelper dbHelper = new DBHelper(context);
                        dbHelper.insertProject("Unable To Fetch", "Unable To Fetch", millisInString, "wow",
                                s.getString("DeviceId", ""), s.getString("deviceno", ""), "server"
                                , "2", context);
                    }

                }
            }
        }

    }

//
//    private void performSubmit(Stock p, Context context) {
//
//        UserRegBean bean = new UserRegBean();
//        bean.email = "work3@mailinator.com";
//        bean.password = "12345678";
//        bean.device_id = Settings.Secure.getString(MyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
//        bean.device_token = PreferencesData.getRegistrationId(context);
//        bean.area_code = p.getSymbol();
//
//        Call<ResponseBody> cl;
//        MainNetworkService service = MyApplication.getSerivce();
//        cl = getCallInstance(service, bean);
//        cl.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                Log.d("Data: success, ", response.toString());
//            }
//            @Override
//            public void onFailure(Throwable t) {
//                Log.d("Data: failure, ", t.toString());
//
//            }
//        });
//        ///new task().execute();
//    }
//
//    public Call<ResponseBody> getCallInstance(MainNetworkService service, UserRegBean bean) {
//        return  service.userSignin("users/sign_in", bean.device_token, bean);
//    }

    private String getaddressFromGEO(double latitude, double longitude) {
       /* Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName();

//        String arrea =  address.substring(0, 10);
        return address;*/
        return "wow";
    }

    public void sendlatlong_to_server(final String latitude, final String longitude, final String datetime) {
        final SharedPreferences s = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
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
        urlBuilder.addQueryParameter("AppStatus", "2");
        urlBuilder.addQueryParameter("MobileDeviceID", s.getString("deviceno", ""));
        //  urlBuilder.addQueryParameter("MobileDeviceID", "9999999999");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("appversion", context.getResources().getString(R.string.version).toString())
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log.d("result", e.getMessage().toString());
                // e.printStackTrace();
                Log.d("result", "service no runnning...............");
                DBHelper dbHelper = new DBHelper(context);
                dbHelper.insertProject(latitude, longitude, datetime, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                        s.getString("DeviceId", ""), s.getString("deviceno", ""), "local", "2", context);

            }

            @Override
            public void onResponse(Call call, final Response response) {
               /* if (!response.isSuccessful()) {
                    Log.d("result", response.toString());
                    // Log.d("addresss ", getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""));
                    //throw new IOException("Unexpected code " + response);
                    SharedPreferences s = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
                    s.getString("DeviceId", "");
                    s.getString("deviceno", "");

                    DBHelper dbHelper = new DBHelper(context);
                    dbHelper.insertProject(latitude, longitude, datetime, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                            s.getString("DeviceId", ""), s.getString("deviceno", ""), "local", context);
                } else {
                    Log.d("result_else", response.toString());
                    SharedPreferences s = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
                    DBHelper dbHelper = new DBHelper(context);
                    dbHelper.insertProject(latitude, longitude, datetime, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                            s.getString("DeviceId", ""), s.getString("deviceno", ""), "server", context);
                }*/


                if (!response.isSuccessful()) {
                    Log.d("result", response.toString());
                    // Log.d("addresss ", getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""));
                    //throw new IOException("Unexpected code " + response);
                    s.getString("DeviceId", "");
                    s.getString("deviceno", "");

                    DBHelper dbHelper = new DBHelper(context);
                    dbHelper.insertProject(latitude, longitude, datetime, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                            s.getString("DeviceId", ""), s.getString("deviceno", ""), "local", "2", context);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("Message");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getString("Response").equals("Success")) {
                                SharedPreferences.Editor se = context.getSharedPreferences("Userdetails", MODE_PRIVATE).edit();
                                se.putString("start_stop", "2");
                                se.commit();
                                DBHelper dbHelper = new DBHelper(context);
                                dbHelper.insertProject(latitude, longitude, datetime, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                                        s.getString("DeviceId", ""), s.getString("deviceno", ""), "server", "2", context);
                            } else {

                                SharedPreferences.Editor sd = context.getSharedPreferences("AppUpgrade", MODE_PRIVATE).edit();
                                sd.putString("AppUpgrade", jsonObject1.getString("Upgrade"));
                                sd.commit();

                                DBHelper dbHelper = new DBHelper(context);
                                dbHelper.insertProject(latitude, longitude, datetime, getaddressFromGEO(17.7167105, 83.306409).replaceAll("',`", ""),
                                        s.getString("DeviceId", ""), s.getString("deviceno", ""), "local", "2", context);
                            }
                        }
                    } catch (Exception e) {

                    }

                }
            }
        });
    }


}
