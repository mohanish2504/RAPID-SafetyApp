package com.example.safetyapp.restarter;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.safetyapp.Globals;
import com.example.safetyapp.ProcessMainClass;

public class RestartServiceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = RestartServiceBroadcastReceiver.class.getSimpleName();
    private static RestartServiceBroadcastReceiver restartSensorServiceReceiver;
    private static JobScheduler jobScheduler;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
            JobService.launchDirectService(context);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            scheduleJob(context);
        }else{
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(context);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context, JobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setOverrideDeadline(0)
                .setPersisted(true)
                .build();
        jobScheduler.schedule(jobInfo);

    }


    private void registerRestarterReceiver(final Context context) {

        Log.d("registerer","registering");

        if (restartSensorServiceReceiver == null)
            restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
        else try {
            context.unregisterReceiver(restartSensorServiceReceiver);
        } catch (Exception e) {
            // not registered
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Globals.RESTART_INTENT);
                try {
                    context.registerReceiver(restartSensorServiceReceiver, filter);
                } catch (Exception e) {
                    try {
                        context.getApplicationContext().registerReceiver(restartSensorServiceReceiver, filter);
                    } catch (Exception ex) {

                    }
                }
            }
        }, 1000);

    }


}
