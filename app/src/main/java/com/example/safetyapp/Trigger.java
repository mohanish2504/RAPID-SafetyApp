package com.example.safetyapp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Trigger {
    private static String TAG = Trigger.class.getSimpleName();
    private static DatabaseReference databaseReference;
    private static String androidId;
    private static TriggerData trigger;

    private void setReference(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Triggers");
        Log.d(TAG,"here");
        androidId="A11";
        trigger = new TriggerData();

    }

    public void registerTrigger(){
        setReference();
        databaseReference.child(androidId).setValue(trigger);
    }


}
