package com.example.safetyapp.Services;


import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.safetyapp.Globals;
import com.example.safetyapp.ProcessMainClass;
import com.example.safetyapp.R;
import com.example.safetyapp.screenreceiver.ScreenOnOffReceiver;
import com.example.safetyapp.utilities.Notification;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Service extends android.app.Service {

    private static ScreenOnOffReceiver screenOnOffReceiver = null;
    private static final String TAG = Service.class.getSimpleName();
    protected static final int NOTIFICATION_ID = 1337;
    private static FusedLocationProviderClient locationClient;
    private static LocationRequest request;
    private static DatabaseReference locationRef;
    private static GeoFire geoFireLoc;
    public static GeoFire geoFireTrigger;
    private static String android_id;
    public static Location locationUser;


    public Service(){super();}


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.i(TAG,"Launching Foreground from onCreate()");
            restartForeground();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG,"Restarting Service from on start Command");
        //count = 0;

        if (intent == null) {
            Log.d(TAG,"intent is null launching new process");
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        return START_STICKY;


    }

    @Override
    public void onDestroy() {

        Log.d(TAG,"Service Destroyed");

        super.onDestroy();
        //unregisterScreenReceiver();
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        //unregisterScreenReceiver();
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    public void restartForeground(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Log.i(TAG,"restarting Foreground");
           // unregisterScreenReceiver();
            try{
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID,notification.setNotification(getApplicationContext(),"App","App is Running", R.drawable.ic_launcher_background));
                Log.i(TAG,"RestartingForeground");
                //startTimer();
                sendScreenBroadcast();
                startLocationUpdates();
            }catch (Exception e){
                Log.i(TAG,"Error during placing notification");
            }
        }
    }


    public void sendScreenBroadcast(){
        Log.d(TAG,"sending Broadcast");
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");


        intentFilter.setPriority(900);

        unregisterScreenReceiver();

        screenOnOffReceiver = new ScreenOnOffReceiver();

        registerReceiver(screenOnOffReceiver, intentFilter);

        Log.d(TAG, "Service onCreate: screenOnOffReceiver is registered.");

    }


    public void unregisterScreenReceiver() {
        if (screenOnOffReceiver != null){
            Log.d(TAG,"Unregister Screen_on_off");
            unregisterReceiver(screenOnOffReceiver);
            Log.d(TAG,"Unregistered Sucessfully");
        }
    }

    private void startLocationUpdates(){
        initializeLocationResources();

        Log.d(TAG,"recveivingUpdates");
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"getting Location");
            // Request location updates and when an update is
            // received, store the location in Firebase
            locationClient.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    Log.d(TAG,String.valueOf(location));
                    if (location != null) {
                        locationUser = location;
                        geoFireLoc.setLocation(android_id, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                Log.d("Locations UPdates", "Completed");
                                //Toast.makeText(getApplicationContext(),"Completed", LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }, null);
        }
        else{
            Log.d(TAG,"not getting Location");
        }
    }

    private void initializeLocationResources(){
        if(request==null){
            request = new LocationRequest();
            request.setInterval(35*60*1000);
            request.setFastestInterval(30*60*1000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        if(locationClient==null)
            locationClient = LocationServices.getFusedLocationProviderClient(this);
        if(locationRef == null)
            locationRef = FirebaseDatabase.getInstance().getReference("Locations");
        if(geoFireLoc == null){
            geoFireLoc = new GeoFire(locationRef);
            geoFireTrigger = new GeoFire(locationRef);
        }
        if(android_id==null)
            android_id = getSharedPreferences("TokenKey",MODE_PRIVATE).getString("Token",null);
    }



}
