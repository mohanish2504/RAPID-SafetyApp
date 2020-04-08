package com.example.safetyapp;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

public class RingtonePlayer {
    private static Uri mUri = Uri.parse("android.resource://com.example.safetyapp/raw/siren");
    public static android.media.Ringtone r;
    public static void setContext(Context context){
        r = RingtoneManager.getRingtone(context.getApplicationContext(), mUri);
    }

}
