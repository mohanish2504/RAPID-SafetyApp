package com.example.safetyapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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


        final DatabaseReference databaseReference,valuesetter;
        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG,"Got Data from firebas");

                Iterable<DataSnapshot> k = dataSnapshot.getChildren();
                for(DataSnapshot t : k){
                   // Log.d(TAG,t.getKey());
                    Tokens.add(t.getKey());
                }

                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Triggers");
                //dbref.removeValue();
                dbref.child(String.valueOf(currentTime)).setValue(Tokens);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"no Data");
            }
        });

        //return Tokens;
    }
}
