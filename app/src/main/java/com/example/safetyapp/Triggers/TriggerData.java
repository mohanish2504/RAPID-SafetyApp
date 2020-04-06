package com.example.safetyapp.Triggers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TriggerData {
    private static List<String> Tokens = new ArrayList<String>();
    private static String TAG = TriggerData.class.getSimpleName();


    public TriggerData() {

    }

    public List<String> getTokens() {
        return Tokens;
    }

    public void setTokens(List<String> tokens) {
        Tokens = tokens;
    }

    public void setData(Context context){
        Log.d(TAG,"setting Data");

        if(!Tokens.isEmpty())Tokens.clear();

        final Date currentTime = Calendar.getInstance().getTime();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Triggers");
        //dbref.removeValue();
        Location location;
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                dbref.child(String.valueOf(currentTime)).setValue(location);
            }
        });

        //return Tokens;
    }
}
