package com.example.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.safetyapp.Services.Service;

public class ProcessMainClass {
    private final static String TAG = ProcessMainClass.class.getSimpleName();
    private static Intent serviceIntent = null;

    public ProcessMainClass() {
    }

    private void setIntent(Context context){
        if(serviceIntent == null) serviceIntent = new Intent(context, Service.class);
    }

    public void launchService(Context context){
        if (context == null) {
            return;
        }

        setIntent(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG,"launching ForegroundService");
            context.startForegroundService(serviceIntent);
        } else {
            Log.d(TAG,"launchig Service");
            context.startService(serviceIntent);
        }
        Log.d(TAG, "ProcessMainClass: start service go!!!!");
    }
}
