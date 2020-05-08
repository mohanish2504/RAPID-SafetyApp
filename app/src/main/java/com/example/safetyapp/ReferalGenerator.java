package com.example.safetyapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ReferalGenerator {
    private static String bound =  "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String referal;
    private static void generateReferal(){
        StringBuilder newreferal = new StringBuilder();
        int l = bound.length();

        for(int i = 0;i<6;i++){
            int random = new Random().nextInt(l);
            char c = bound.charAt(random);
            newreferal.append(c);
        }

         referal = newreferal.toString();
        Log.d("Referal",referal);
    }

    public static void checkForReferal(final String UID){
       // Log.d("here",referal);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Referals");
        databaseReference.orderByKey().equalTo(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    if(referal==null)generateReferal();
                    databaseReference.child(UID).setValue(referal);
                }else{
                    //DataSnapshot dataSnapshot1 = dataSnapshot.getValue();
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        referal = (String) dataSnapshot1.getValue();
                    }
                }
                Log.d("Referal",referal);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String getReferal(){return referal;}

}
