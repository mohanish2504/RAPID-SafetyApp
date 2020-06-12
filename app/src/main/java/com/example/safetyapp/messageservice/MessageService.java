package com.example.safetyapp.messageservice;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.safetyapp.Globals;
import com.example.safetyapp.HelpRequests;
import com.example.safetyapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessageService extends FirebaseMessagingService {
    private static String channelID = "Notification Channel";
    private static String TAG = FirebaseMessagingService.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Service started");

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DeviceTokens");
        String number = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");

        if(number!=null){
            databaseReference.child(number).setValue(s);
        }

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG,"Service started");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG,"Message Received");
        Map<String,String> map;
        map = remoteMessage.getData();

        HelpRequests.UserInNeed userInNeed = new HelpRequests.UserInNeed(map);
        Long time = Long.valueOf(map.get("time"));

        if(HelpRequests.addUser(time,userInNeed)){
            Log.d(TAG, String.valueOf(HelpRequests.currentRequests()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showNotification(map.get("title"),map.get("body"));
            }
            else{
                notifyFunc(map.get("title"),map.get("body"));
            }
            Globals.pendingrequests++;
            Intent sendBroadcastIntent = new Intent(Globals.BROADCAST);
            sendBroadcast(sendBroadcastIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(String title, String body){
        Notification.Builder notification = new Notification.Builder(this,"Notification Channel")
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_background);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(998,notification.build());
    }


    public void notifyFunc(String title, String body){
        Log.d("message",body);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "Notification Channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,notification.build());
    }


}
