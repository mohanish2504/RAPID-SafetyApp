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

import com.example.safetyapp.Trigger;

import java.util.concurrent.TimeUnit;

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
                pressCounter = 0;
        }

        Log.d("difference",Integer.toString(pressCounter)+" "+Long.toString(difference));

        if(pressCounter==5){
            pressCounter = 0;
            try {
                overrideDND(context);
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                r.play();
                trigger.registerTrigger();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*currentClickTime = System.currentTimeMillis();
        sharedPref = context.getApplicationContext().getSharedPreferences("Click_Recorder",Context.MODE_PRIVATE) ;
        editor = sharedPref.edit();

        /*if(!sharedPref.contains("click_counter")){
            editor.putLong("totalTime",0);
            editor.putInt("click_counter",0);
            editor.commit();
            click_counter = sharedPref.getInt("click_counter",0);
            Log.d("click_counter here",Integer.toString(click_counter));
        }else{
            click_counter = sharedPref.getInt("click_counter",0);
            ++click_counter;
            editor.putInt("click_counter",click_counter);
            editor.commit();
            Log.d("click_counter hi",Integer.toString(click_counter));
        }
        String action = intent.getAction();

        if(Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_SCREEN_ON.equals(action))
        {
            if(!sharedPref.contains("lastClickTime")){
                editor.putLong("lastClickTime", System.currentTimeMillis());
                Log.d("new","new entry");
                editor.apply();
            }
            else{
                lastClickTime = sharedPref.getLong("lastClickTime",0);
                difference = currentClickTime - lastClickTime;
                Log.d("difference",Double.toString((double)TimeUnit.MILLISECONDS.toMillis(currentClickTime - lastClickTime)));

                if(!test){
                    if(difference > highest && difference < 2000){
                        highest = difference;
                    }
                    if(least > difference){
                        least=difference;
                    }
                    if(difference>2000){
                        Log.d("rem","removed");
                        editor.remove("lastClickTime");
                        editor.commit();
                        i=-1;

                    }
                }

                editor.putLong("lastClickTime",currentClickTime);
                editor.apply();
                Log.d("high",Long.toString(highest));
                Log.d("low",Long.toString(least));
                Log.d("click",Integer.toString(clickCounter));

                if((difference<=highest) && test ){

                    ++clickCounter;
                    if (clickCounter == 3){
                        try {
                            overrideDND(context);
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                            r.play();
                            trigger.registerTrigger();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        clickCounter = 0;
                    }
                }
                else{
                    clickCounter = 0;
                }

            }

            ++i;
            if(i==5 && !test){
                Log.d("rem","removed");
                editor.remove("lastClickTime");
                i=0;
                test = true;
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                    r.play();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                editor.commit();
            }
        }*/
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
