package com.example.safetyapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.Firebase.SendData;
import com.example.safetyapp.restarter.RestartServiceBroadcastReceiver;
import com.example.safetyapp.screenreceiver.ScreenOnOffReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    private ScreenOnOffReceiver screenOnOffReceiver = null;
    private static String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int click_counter ;
    long totalTime;
    TextView textView;
    private static String channelID = "Notification Channel";
    private static String Token;
    DatabaseReference databaseReference;
    SendData sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.disp);
        sharedPref = getApplicationContext().getSharedPreferences("Counter", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        sendData = new SendData();

        checkCounter();

        setToken();

        createMethod();

        scheduleJob();

        /*FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        //Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/

    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy()","Destroyed");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Log.d("onDestroy()","Starting Process");
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }

    }


    //@Override
    protected void onResume() {
        super.onResume();


    }*/

    private void checkCounter(){
        if(sharedPref.contains("click_counter")){
            click_counter = sharedPref.getInt("click_counter",0);
            if(click_counter >= 10){
                editor.putInt("click_counter",0);
                totalTime = sharedPref.getLong("totalTime",0);
                editor.putLong("totalTime",0);
                editor.commit();
                Log.d("AverageTime",Long.toString(totalTime/10));
                textView.setText(Long.toString(totalTime/10));
            }
        }
    }

    private void setToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Token = token;
                        sendData.sendToken("",token);

                    }
                });
    }

    private void createMethod(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Project";
            String description = "new message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void scheduleJob(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // Log.d("MainActivity","Starting Process");
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }




}
