package com.example.maple.locationupdatefrequent.Helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.example.maple.locationupdatefrequent.LocationUpdatesBroadcastReceiver;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Constants {

    private static String TAG = Constants.class.getSimpleName();
    public static final String GEOFENCE_ID_TECH_M = "TECH_M";
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;
    /**
     * Map for storing information about stanford university in the Stanford.
     */
    public static final HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<String, LatLng>();

    static {
        Log.d(TAG, "static block");
        // Tech Mahindra - (17.732966, 83.313632)
        //AREA_LANDMARKS.put(GEOFENCE_ID_TECH_M, new LatLng(17.732667, 83.313822));
        AREA_LANDMARKS.put(GEOFENCE_ID_TECH_M, new LatLng(17.724358, 83.3067562));
    }

    //tech - 17.732667, 83.313822
// old hospital     17.731201,83.311212
//        17.7340027, 83.3109761
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Stores the lat / long pairs in a text file
    public static final String LOCATION_FILE = "sdcard/location.txt";
    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";

    public static final String RUNNING = "runningInBackground"; // Recording data in background

    public static final String APP_PACKAGE_NAME = "com.blackcj.locationtracker";

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
            "com.google.android.gms.location.sample.backgroundlocationupdates.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS =
            "com.google.android.gms.location.sample.backgroundlocationupdates.STATUS";

    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }


  /*  AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    Intent intent21 = new Intent(this, LocationUpdatesBroadcastReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent21, 0);
    // cal.add(Calendar.SECOND, 5);
    // alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    //alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,cal.getTimeInMillis(), pendingIntent);

         alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +60000, pendingIntent);
    // alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), ((Integer.parseInt(String.valueOf(1))) * 60000 ), pendingIntent);
    // alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntent);

    SharedPreferences.Editor s = getSharedPreferences("dump",MODE_PRIVATE).edit();
        s.putInt("value",3);
        s.commit();*/
}
