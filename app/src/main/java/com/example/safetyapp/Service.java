package com.example.safetyapp;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.safetyapp.screenreceiver.ScreenOnOffReceiver;
import com.example.safetyapp.utilities.Notification;

import java.util.Timer;
import java.util.TimerTask;

public class Service extends android.app.Service {

    private static ScreenOnOffReceiver screenOnOffReceiver = null;
    private static final String TAG = Service.class.getSimpleName();
    protected static final int NOTIFICATION_ID = 1337;
    private int count ;

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

        Log.d(TAG,"Restating Service");
        //count = 0;

        if (intent == null) {
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
                startForeground(NOTIFICATION_ID,notification.setNotification(getApplicationContext(),"App","App is Running",R.drawable.ic_launcher_background));
                Log.i(TAG,"RestartingForeground");
                //startTimer();
                sendScreenBroadcast();
            }catch (Exception e){
                Log.i(TAG,"Error during placing notification");
            }
        }
    }
    private static Timer timer;
    private static TimerTask timerTask;

    public void startTimer(){

        Log.i(TAG, "Starting Timer");

       // stoptimertask();
        timer = new Timer();

        //initializeTimerTask();

        Log.i(TAG, "Scheduling...");

        timer.schedule(timerTask,1000,1000);
    }

    public void stoptimertask(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask(){
        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("Timer","Timer =====>"+(count++) );
            }
        };
    }

    public void sendScreenBroadcast(){
        Log.d(TAG,"sending Broadcast");
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("android.intent.action.LOCKED_BOOT_COMPLETED");

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
}
