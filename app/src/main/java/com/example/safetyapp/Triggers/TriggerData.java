package com.example.safetyapp.Triggers;

import android.util.Log;

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

    public void setData(){
        Log.d(TAG,"setting Data");

        if(!Tokens.isEmpty())Tokens.clear();

        final Date currentTime = Calendar.getInstance().getTime();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Triggers");
        //dbref.removeValue();
        dbref.child(String.valueOf(currentTime)).setValue("true");
        //return Tokens;
    }
}