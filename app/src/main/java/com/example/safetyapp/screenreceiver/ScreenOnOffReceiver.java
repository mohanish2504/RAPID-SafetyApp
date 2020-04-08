package com.example.safetyapp.screenreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.safetyapp.Triggers.Trigger;

import static android.app.NotificationManager.INTERRUPTION_FILTER_ALL;
import static android.content.Context.NOTIFICATION_SERVICE;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    public final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
    long lastPress;
    long currentPress;
    long difference;
    long least=Long.MAX_VALUE,highest=0;
    boolean test = false;
    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor editor = null;
    private static int i=0,clicks=0;
    private static int pressCounter = 0;
    private static Trigger trigger = new Trigger();


    @Override
    public void onReceive(Context context, Intent intent) {

        if(sharedPref == null || editor == null){
            sharedPref = context.getSharedPreferences("Lastpress",Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        currentPress = System.currentTimeMillis();

        Log.d("currentPress", Long.toString(currentPress));


        lastPress = sharedPref.getLong("lastPress",currentPress);

        Log.d("lastPress", Long.toString(lastPress));

        editor.putLong("lastPress",currentPress);

        difference = currentPress - lastPress;

        editor.commit();

        if(difference <= 1500) pressCounter++;

        else {
                pressCounter = 1;
        }

        Log.d("difference",Integer.toString(pressCounter)+" "+Long.toString(difference));

        if(pressCounter==5){
            pressCounter = 0;
            try {
                overrideDND(context);
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                r.play();
                trigger.registerTrigger(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void overrideDND(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Log.d("overrideDND()","about to ask for permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Log.d("overrideDND()","asking for permissions");
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            mNotificationManager.setInterruptionFilter(INTERRUPTION_FILTER_ALL);

        }
    }

}
