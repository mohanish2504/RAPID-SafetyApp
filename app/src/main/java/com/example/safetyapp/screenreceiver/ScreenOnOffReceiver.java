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
import android.widget.Toast;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.example.safetyapp.RingtonePlayer;
import com.example.safetyapp.Triggers.Trigger;

import static android.app.NotificationManager.INTERRUPTION_FILTER_ALL;
import static android.content.Context.NOTIFICATION_SERVICE;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    private static final String TAG = ScreenOnOffReceiver.class.getSimpleName();
    long lastPress;
    long currentPress;
    long difference;
    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor editor = null;
    private static int pressCounter = 0;
    private static Trigger trigger = new Trigger();
    private  Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        if(sharedPref == null || editor == null){
            sharedPref = context.getSharedPreferences("Info",Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        currentPress = System.currentTimeMillis();

        Log.d("currentPress", Long.toString(currentPress));

        lastPress = sharedPref.getLong("lastPress",currentPress);

        Log.d("lastPress", Long.toString(lastPress));

        editor.putLong("lastPress",currentPress);

        difference = currentPress - lastPress;

        editor.apply();

        if(difference <= 1500) pressCounter++;

        else {
                pressCounter = 1;
        }

        alert();

        Log.d("difference",Integer.toString(pressCounter)+" "+Long.toString(difference));

    }

    private void alert(){
        long minimumTriggerTime = 2*60*1000;
        long currentTriggerTime = System.currentTimeMillis();
        long previousTriggerTime = sharedPref.getLong("LastTrigger",currentTriggerTime);

        /*if(pressCounter==5){
            pressCounter = 0;
            try {
                Log.d(TAG,"Trigger Accepted");
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Uri mUri = Uri.parse("android.resource://com.example.safetyapp/raw/siren");
                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), mUri);
                r.play();
                editor.putLong("LastTrigger",currentTriggerTime);
                trigger.registerTrigger(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        if(pressCounter==5 && ((currentTriggerTime-previousTriggerTime>=minimumTriggerTime) || (currentTriggerTime-previousTriggerTime == 0))){
            pressCounter = 0;
            try {
                Log.d(TAG,"Trigger Accepted");

                RingtonePlayer.setContext(context);
                RingtonePlayer.r.play();

                editor.putLong("LastTrigger",currentTriggerTime);
                trigger.registerTrigger(context);

                editor.putString("TriggerStatus","ON").apply();

                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(pressCounter == 5) {
            pressCounter = 0;
            Log.d(TAG,"Trigger Not Accepted");
            Toast.makeText(context,"You must wait for atleast 2 minutes to make new Trigger request",Toast.LENGTH_LONG).show();
        }
    }

}
