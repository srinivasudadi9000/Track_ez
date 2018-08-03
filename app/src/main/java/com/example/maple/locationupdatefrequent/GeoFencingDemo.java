package com.example.maple.locationupdatefrequent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maple.locationupdatefrequent.Activity.AdminMessages;
import com.example.maple.locationupdatefrequent.Activity.EditPhone;
import com.example.maple.locationupdatefrequent.Activity.GetUserReports;
import com.example.maple.locationupdatefrequent.Activity.Messages;
import com.example.maple.locationupdatefrequent.Activity.SplashScreen;
import com.example.maple.locationupdatefrequent.Activity.UploadQuestion;
import com.example.maple.locationupdatefrequent.Helper.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.master.permissionhelper.PermissionHelper;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GeoFencingDemo extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {


    ComponentName component;
    Marker marker;
    ProgressDialog pDialog;

    private static final String TAG = GeoFencingDemo.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_SMS = 35;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    // FIXME: 5/16/17
    private static final long UPDATE_INTERVAL = 5 * 6000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    // FIXME: 5/14/17
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * The entry point to Google Play Services.
     */
    private GoogleApiClient mGoogleApiClient;

    // UI Widgets.
    //private Button mRequestUpdatesButton;
    //private Button mRemoveUpdatesButton;
    //private TextView mLocationUpdatesResultView;
    private GeofencingRequest geofencingRequest;
    private PendingIntent pendingIntent;
    private boolean isMonitoring = false;
    private GoogleMap googleMap;
    private IntentFilter statusIntentFilter;
    private BroadcastReceiver mDownloadStateReceiver;
    private SharedPreferences sharedPreferences, START_STOP;

    Activity act;
    private Circle circle;
    TextView clickme;
    Geocoder geocoder;
    SupportMapFragment mapFragment;
    private PermissionHelper permissionHelper;
    FloatingActionButton start_fab;
    TextView statsu_tv, tapstatus_tv, device_id;
    CardView status_cv, logout_cv, mesages_cardview, admin_msg_cv, recentrep_cv,dailyreport_cv;
    ImageView back_img, refresh_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;
        ((AppCompatActivity) act).getSupportActionBar().hide();
        back_img = findViewById(R.id.back_img);
        refresh_img = findViewById(R.id.refresh_img);
        back_img.setVisibility(View.GONE);
        refresh_img.setVisibility(View.GONE);
        status_cv = findViewById(R.id.status_cv);
        logout_cv = findViewById(R.id.logout_cv);
        mesages_cardview = findViewById(R.id.mesages_cardview);
        admin_msg_cv = findViewById(R.id.admin_msg_cv);
        recentrep_cv = findViewById(R.id.recentrep_cv);
        dailyreport_cv = findViewById(R.id.dailyreport_cv);
        statsu_tv = findViewById(R.id.statsu_tv);
        tapstatus_tv = findViewById(R.id.tapstatus_tv);
        device_id = findViewById(R.id.device_id);
        start_fab = findViewById(R.id.start_fab);
        start_fab.setOnClickListener(GeoFencingDemo.this);
        status_cv.setOnClickListener(GeoFencingDemo.this);
        logout_cv.setOnClickListener(GeoFencingDemo.this);
        recentrep_cv.setOnClickListener(GeoFencingDemo.this);
        dailyreport_cv.setOnClickListener(GeoFencingDemo.this);
        mesages_cardview.setOnClickListener(GeoFencingDemo.this);
        admin_msg_cv.setOnClickListener(GeoFencingDemo.this);
        component = new ComponentName(act, LocationUpdatesBroadcastReceiver.class);
        geocoder = new Geocoder(this, Locale.getDefault());

        sharedPreferences = getSharedPreferences("location_date_storage", MODE_PRIVATE);
        START_STOP = getSharedPreferences("START_STOP", MODE_PRIVATE);
        if (START_STOP.getString("status", "").length() > 0) {
            if (START_STOP.getString("status", "").equals("STOP")) {
                statsu_tv.setTextColor(getResources().getColor(R.color.green));
                statsu_tv.setText(START_STOP.getString("status", "").toString());
            } else {
                statsu_tv.setTextColor(getResources().getColor(R.color.white));
                statsu_tv.setText(START_STOP.getString("status", "").toString());
            }
        } else {
            statsu_tv.setTextColor(getResources().getColor(R.color.white));
            statsu_tv.setText("START");
        }

        SharedPreferences ss = getSharedPreferences("Userdetails", MODE_PRIVATE);
        device_id.setText(ss.getString("deviceno", "") + " " + android.os.Build.MANUFACTURER);
        Log.d(TAG, "onCreate");
        // The filter's action is BROADCAST_ACTION
        statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);

        //mRequestUpdatesButton = (Button) findViewById(R.id.request_updates_button);
        //mRemoveUpdatesButton = (Button) findViewById(R.id.remove_updates_button);
        //mLocationUpdatesResultView = (TextView) findViewById(R.id.location_updates_result);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //permissions
        //  isWriteStoragePermissionGranted();


//        if (!checkPermissionsSMS()){
//            requestPermissionsSMS();
//        }


        //  buildGoogleApiClient();


        mapFragment.getMapAsync(this);


        mDownloadStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Broadcast", "RECEIVED!!");
            }
        };
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mDownloadStateReceiver, statusIntentFilter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume");
////        updateButtonsState(LocationRequestHelper.getRequesting(this));
////        mLocationUpdatesResultView.setText(LocationResultHelper.getSavedLocationResult(this));
//    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
      /*  PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);*/
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
//        getMenuInflater().inflate(R.menu.menu_map_activity, menu);
//        if (isMonitoring) {
//            menu.findItem(R.id.action_start_monitor).setVisible(false);
//            menu.findItem(R.id.action_stop_monitor).setVisible(true);
//        } else {
//            menu.findItem(R.id.action_start_monitor).setVisible(true);
//            menu.findItem(R.id.action_stop_monitor).setVisible(false);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d(TAG, "onOptionsItemSelected");
//        switch (item.getItemId()) {
//            case R.id.action_start_monitor:
//                startGeofencing();
//                break;
//            case R.id.action_stop_monitor:
//                stopGeoFencing();
//                break;
//            case R.id.action_settings:
////                changeGeofence();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }


//    private void changeGeofence() {
//
//        if (isMonitoring) {
//            Toast.makeText(GeoFencingDemo.this, "Please stop monitoring and try again.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // get prompts.xml view
//        LayoutInflater li = LayoutInflater.from(this);
//        View promptsView = li.inflate(R.layout.custom_dialog, null);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//
//        // set prompts.xml to alertdialog builder
//        alertDialogBuilder.setView(promptsView);
//
//        final EditText etLat = (EditText) promptsView
//                .findViewById(R.id.etLat);
//        final EditText etLong = (EditText) promptsView
//                .findViewById(R.id.etLong);
//        final EditText etRadius = (EditText) promptsView
//                .findViewById(R.id.etRadius);
//        final EditText etLocationName = (EditText) promptsView
//                .findViewById(R.id.etLocationName);
//
//
//        Button btnCancel = (Button) promptsView.findViewById(R.id.btnCancel);
//        Button btnSave = (Button) promptsView.findViewById(R.id.btnSave);
//
//        // set dialog message
//        alertDialogBuilder
//                .setCancelable(false);
//
//        // create alert dialog
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.cancel();
//            }
//        });
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                validateFields(etLat, etLong, etRadius,etLocationName, alertDialog);
//
//            }
//        });
//
//
//    }

//    private void validateFields(EditText etLat, EditText etLong, EditText etRadius, EditText etLocationName, AlertDialog alertDialog) {
//
//
//        if (etLat.getText().toString().isEmpty()) {
//            etLat.setError("Enter latitude");
//            return;
//        } else if (etLong.getText().toString().isEmpty()) {
//            etLong.setError("Enter longitude");
//            return;
//        } else if (etRadius.getText().toString().isEmpty()) {
//            etRadius.setError("Enter radius");
//            return;
//        }
////        else if (etLocationName.getText().toString().isEmpty()){
////            etLocationName.setError("Enter location name");
////        }
//
//
//        double latitude = Double.parseDouble(etLat.getText().toString());
//        double longitude = Double.parseDouble(etLong.getText().toString());
//        float radiusInMtrs = Float.parseFloat(etRadius.getText().toString());
//        String area  = getaddressFromGEO(latitude, longitude);
//
//        saveDataInSharedPreferences(latitude, longitude, radiusInMtrs,area,  alertDialog);
//
//    }

//    private void saveDataInSharedPreferences(double latitude, double longitude, float radiusInMtrs, String area, AlertDialog alertDialog) {
//        alertDialog.cancel();
//
//        sharedPreferences.edit()
//                .putFloat("radius", radiusInMtrs)
//                .putString("latitude", String.valueOf(latitude))
//                .putString("longitude", String.valueOf(longitude))
//                .putString("area", area)
//                .apply();
//
//
//        showMap();
//    }

    private String getaddressFromGEO(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
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
        return address;
    }

//    private void showMap() {
//
//        float rad = sharedPreferences.getFloat("radius", 0);
//
//        if (rad != 0) {
//
//            marker.remove();
//            circle.remove();
//
//            double lat = Double.parseDouble(sharedPreferences.getString("latitude", null));
//            double longit = Double.parseDouble(sharedPreferences.getString("longitude", null));
//           String area = sharedPreferences.getString("area", "Unknown area");
//            this.googleMap = googleMap;
//            LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID_TECH_M);
//            LatLng latLng1 = new LatLng(lat, longit);
//            marker =  googleMap.addMarker(new MarkerOptions().position(latLng1).title(area));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17f));
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//
//            googleMap.setMyLocationEnabled(true);
//
//            circle = googleMap.addCircle(new CircleOptions()
//                    .center(new LatLng(latLng1.latitude, latLng1.longitude))
//                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
//                    .strokeColor(Color.RED)
//                    .strokeWidth(4f));
//
//        }
//
//    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                Toast.makeText(getBaseContext(), "All Location Settings are satisfied herer The client can initialize", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(GeoFencingDemo.this,
                                0);
                        String xx = String.valueOf(resolvable.getStatusCode());
                        Log.d("d", xx);
                        //Toast.makeText(getBaseContext(),,Toast.LENGTH_SHORT).show();
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Toast.makeText(getBaseContext(), "cancel the dialog", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String xx = String.valueOf(requestCode);
        String yy = String.valueOf(resultCode);
        Toast.makeText(getBaseContext(), "REquest code :" + xx + "  request res : " + yy, Toast.LENGTH_SHORT).show();
        if (resultCode == 0) {
            createLocationRequest();
        }
    }

    private void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        LocationRequestHelper.setRequesting(this, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Log.d(TAG, "getPendingIntent");
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getCancelIntent() {
        Log.d(TAG, "getPendingIntent");
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        final String text = "Connection suspended";
        Log.w(TAG, text + ": Error code: " + i);
        showSnackbar("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        final String text = "Exception while connecting to Google Play services";
        Log.w(TAG, text + ": " + connectionResult.getErrorMessage());
        showSnackbar(text);
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        Log.d(TAG, "showSnackbar");
        View container = findViewById(R.id.activity_main);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


    //
    private void requestPermissionsSMS() {
        Log.d(TAG, "requestPermissions");
        boolean shouldProvideSmsPer =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS);
        if (shouldProvideSmsPer) {
            Log.i(TAG, "Displaying permission rationale to provide .");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_sms,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(GeoFencingDemo.this,
                                    new String[]{Manifest.permission.SEND_SMS},
                                    REQUEST_PERMISSIONS_REQUEST_CODE_SMS);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(GeoFencingDemo.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_PERMISSIONS_REQUEST_CODE_SMS);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(TAG, "onSharedPreferenceChanged");
        if (s.equals(LocationResultHelper.KEY_LOCATION_UPDATES_RESULT)) {
            //mLocationUpdatesResultView.setText(LocationResultHelper.getSavedLocationResult(this));
        } else if (s.equals(LocationRequestHelper.KEY_LOCATION_UPDATES_REQUESTED)) {
            updateButtonsState(LocationRequestHelper.getRequesting(this));
        }
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    public void requestLocationUpdates(View view) {
        Log.d(TAG, "requestLocationUpdates");
        try {
            Log.i(TAG, "Starting location updates");
            LocationRequestHelper.setRequesting(this, true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            LocationRequestHelper.setRequesting(this, false);
            e.printStackTrace();
        }
    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    public void removeLocationUpdates(View view) {
        Log.i(TAG, "Removing location updates");
        LocationRequestHelper.setRequesting(this, false);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getPendingIntent());
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
    private void updateButtonsState(boolean requestingLocationUpdates) {
        Log.d(TAG, "updateButtonsState");
        if (requestingLocationUpdates) {
            //mRequestUpdatesButton.setEnabled(false);
            //mRemoveUpdatesButton.setEnabled(true);
        } else {
            //mRequestUpdatesButton.setEnabled(true);
            //mRemoveUpdatesButton.setEnabled(false);
        }
    }


    private void startLocationMonitor() {
        Log.d(TAG, "start location monitor");
        /*LocationRequest locationRequest = LocationRequest.create()
                .setInterval(20000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude());
                    /*if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }
                    markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                    markerOptions.title("Current Location");

                    if(googleMap != null)
                        currentLocationMarker = googleMap.addMarker(markerOptions);
                    Log.d(TAG, "Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude());*/
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    private void startGeofencing() {
        Log.d(TAG, "Start geofencing monitoring call");

        if (!isReceiverEnabled()) {
            act.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        pendingIntent = getGeofencePendingIntent();
        geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofence(getGeofence())
                .build();

        if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Google API client not connected");
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofencingRequest, pendingIntent).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Successfully Geofencing Connected");
                        } else {
                            Log.d(TAG, "Failed to add Geofencing " + status.getStatus());
                        }
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        isMonitoring = true;
        invalidateOptionsMenu();
    }

    @NonNull
    private Geofence getGeofence() {
        Log.d(TAG, "getGeofence");

        float rad = sharedPreferences.getFloat("radius", 0);

        if (rad != 0) {

            double lat = Double.parseDouble(sharedPreferences.getString("latitude", null));
            double longit = Double.parseDouble(sharedPreferences.getString("longitude", null));
            String area = sharedPreferences.getString("area", "unKnown area 1");
            LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID_TECH_M);
            return new Geofence.Builder()
                    .setRequestId(area)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(lat, longit, rad)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

        } else {

            LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID_TECH_M);
            return new Geofence.Builder()
                    .setRequestId(Constants.GEOFENCE_ID_TECH_M)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(latLng.latitude, latLng.longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
        }

    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d(TAG, "getGeofencePendingIntent");
        if (pendingIntent != null) {
            return pendingIntent;
        }
//        Intent intent = new Intent(this, GeofenceRegistrationService.class);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return null;
    }

    private void stopGeoFencing() {
        Log.d(TAG, "stopGeoFencing");
        pendingIntent = getGeofencePendingIntent();
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess())
                            Log.d(TAG, "Stop geofencing");
                        else
                            Log.d(TAG, "Not stop geofencing");
                    }
                });
        isMonitoring = false;
        invalidateOptionsMenu();

        if (isReceiverEnabled()) {
            act.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        float rad = sharedPreferences.getFloat("radius", 0);


        if (rad != 0) {
            double lat = Double.parseDouble(sharedPreferences.getString("latitude", null));
            double longit = Double.parseDouble(sharedPreferences.getString("longitude", null));
            String area = sharedPreferences.getString("area", "unKnown area 1");

            this.googleMap = googleMap;
            LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID_TECH_M);
            LatLng latLng1 = new LatLng(lat, longit);
            marker = googleMap.addMarker(new MarkerOptions().position(latLng1).title(area));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17f));
            googleMap.setMyLocationEnabled(true);

            circle = googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(latLng1.latitude, latLng1.longitude))
                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
                    .strokeColor(Color.RED)
                    .strokeWidth(4f));


        } else {

            this.googleMap = googleMap;
            LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID_TECH_M);
            marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("TECH MAHINDRA"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
            googleMap.setMyLocationEnabled(true);

            circle = googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(latLng.latitude, latLng.longitude))
                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
                    .strokeColor(Color.RED)
                    .strokeWidth(4f));

        }

    }

    public boolean isReceiverEnabled() {
        boolean b = false;
        int status = act.getPackageManager().getComponentEnabledSetting(component);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.d(TAG, "receiver is enabled");
            b = true;
        } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            Log.d(TAG, "receiver is disabled");
            b = false;
        }
        return b;
    }


    public void isWriteStoragePermissionGranted() {

        permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(act, "granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onPermissionGranted() called");

                buildGoogleApiClient();

                mapFragment.getMapAsync(GeoFencingDemo.this);

                isMonitoring = true;

            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",", grantedPermission) + "]");
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(GeoFencingDemo.this, "permission denied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onPermissionDenied() called");
            }

            @Override
            public void onPermissionDeniedBySystem() {
                Log.d(TAG, "onPermissionDeniedBySystem() called");
                permissionHelper.openAppDetailsActivity();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_fab:

                vibrate();
                if (statsu_tv.getText().toString().equals("START")) {
                    isWriteStoragePermissionGranted();
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .registerOnSharedPreferenceChangeListener(this);
                    // AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    //  Intent w = new Intent(this, LocationUpdatesBroadcastReceiver.class);
                    ///  PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, w, 0);
                    //  Calendar cal2 = Calendar.getInstance();
                    //  alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 60000, pendingIntent);
                    AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    Intent w = new Intent(this, LocationUpdatesBroadcastReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, w, 0);
                    Calendar cal2 = Calendar.getInstance();

                    // cal.add(Calendar.SECOND, 5);
                    // alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    //alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,cal.getTimeInMillis(), pendingIntent);

                    if (android.os.Build.MANUFACTURER.equals("LeMobile")) {
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 60000, pendingIntent);
                    } else if (android.os.Build.MANUFACTURER.equals("vivo")) {
                        Toast.makeText(getBaseContext(), "vivo mobile", Toast.LENGTH_SHORT).show();
                        //  alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 60000, pendingIntent);
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 30000, pendingIntent);
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 60000, pendingIntent);
                            // only for gingerbread and newer versions
                        } else {
                            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 60000, pendingIntent);
                        }
                    }
                    buildGoogleApiClient();
                    statsu_tv.setTextColor(getResources().getColor(R.color.green));
                    statsu_tv.setText("STOP");
                    tapstatus_tv.setText("Tap To Stop");
                    PackageManager pm = this.getPackageManager();
                    ComponentName componentName = new ComponentName(this, LocationUpdatesBroadcastReceiver.class);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                   /* clearApplicationData();
                    getCancelIntent();*/
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .unregisterOnSharedPreferenceChangeListener(this);
                    PackageManager pm = this.getPackageManager();
                    ComponentName componentName = new ComponentName(this, LocationUpdatesBroadcastReceiver.class);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    statsu_tv.setTextColor(getResources().getColor(R.color.white));
                    statsu_tv.setText("START");
                    tapstatus_tv.setText("Tap To Start");
                }

                break;

            case R.id.status_cv:
                Intent attndance_lo = new Intent(GeoFencingDemo.this, Attendance_lo.class);
                startActivity(attndance_lo);
                break;
            case R.id.dailyreport_cv:
                Intent dailyreport = new Intent(GeoFencingDemo.this, UploadQuestion.class);
                startActivity(dailyreport);
                break;
            case R.id.logout_cv:
                if (statsu_tv.getText().toString().equals("STOP")) {
                    showDialog(GeoFencingDemo.this, "Before Logout Please Stop Service", "no");
                } else {
                    clearApplicationData();
                }
                break;
            case R.id.mesages_cardview:
                Intent messages = new Intent(GeoFencingDemo.this, Messages.class);
                startActivity(messages);
                break;
            case R.id.admin_msg_cv:
                Intent adminmsg = new Intent(GeoFencingDemo.this, AdminMessages.class);
                startActivity(adminmsg);
                break;
            case R.id.recentrep_cv:
                Intent recentreports = new Intent(GeoFencingDemo.this, GetUserReports.class);
                startActivity(recentreports);
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
                    Intent dashboard = new Intent(GeoFencingDemo.this, GeoFencingDemo.class);
                    startActivity(dashboard);
                    finish();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor start_stop = getSharedPreferences("START_STOP", MODE_PRIVATE).edit();
        start_stop.putString("status", statsu_tv.getText().toString());
        start_stop.commit();
    }

    public void clearApplicationData() {
        getApplicationContext().getSharedPreferences("START_STOP", MODE_PRIVATE).edit().clear().commit();
        getApplicationContext().getSharedPreferences("location_date_storage", MODE_PRIVATE).edit().clear().commit();
        getApplicationContext().getSharedPreferences("Userdetails", MODE_PRIVATE).edit().clear().commit();

        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }

            Intent Spl = new Intent(GeoFencingDemo.this, SplashScreen.class);
            startActivity(Spl);
            finish();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}


