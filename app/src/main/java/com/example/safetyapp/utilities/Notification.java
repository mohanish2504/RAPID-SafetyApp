
package com.example.safetyapp.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;


public class Notification {
    private PendingIntent notificationPendingIntent;

    public android.app.Notification setNotification(Context context, String title, String text, int icon) {
        if (notificationPendingIntent == null) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        android.app.Notification notification;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Permanent Notification";
            //mContext.getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_LOW;

            String CHANNEL_ID = "com.example.safety.channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            //String description = mContext.getString(R.string.notifications_description);
            String description = "I would like to receive travel alerts and notifications for:";
            channel.setDescription(description);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            notification = notificationBuilder
                    .setSmallIcon(icon)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(text)
                    //.setPriority(android.app.Notification.PRIORITY_HIGH)
                    .setContentIntent(notificationPendingIntent)
                    .build();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            notification = new NotificationCompat.Builder(context, "channel")
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build();
        } else {
            notification = new NotificationCompat.Builder(context, "channel")
                    // to be defined in the MainActivity of the app
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build();
        }

        return notification;
    }
}
