package com.example.safetyapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.safetyapp.user.ReferalActivity;
import com.example.safetyapp.user.signUpActivity;
import com.example.safetyapp.user.verify_phone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.Inet4Address;
import java.util.Random;

public class ReferalGenerator {
    private static String bound =  "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String referal;
    private static void generateReferal(){
        StringBuilder newreferal = new StringBuilder();
        int l = bound.length();
        referal = null;
        for(int i = 0;i<6;i++){
            int random = new Random().nextInt(l);
            char c = bound.charAt(random);
            newreferal.append(c);
        }

         referal = newreferal.toString();
        Log.d("Referal",referal);
    }

    public static void checkForReferal(final String phone, final Context context, final boolean isNewUser, final boolean noFlags){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Referals");
        databaseReference.orderByKey().equalTo(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    generateReferal();
                    databaseReference.child(phone).setValue(referal);
                }else{
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        referal = (String) dataSnapshot1.getValue();
                    }
                }
                Log.d("Referal",referal);
                Globals.REFERAL=referal;
                if(isNewUser)LaunchActivitySignUp(context);
                else if(noFlags) LaunchActivityReferal_withNoFlags(context);
                else LaunchActivityReferal(context);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String getReferal(){return referal;}
    public static void LaunchActivityReferal(Context context){
        Intent i = new Intent(context, ReferalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    public static void LaunchActivityReferal_withNoFlags(Context context){
        Intent i = new Intent(context, ReferalActivity.class);
        context.startActivity(i);
    }

    public static void LaunchActivitySignUp(Context context){
        Intent intent = new Intent(context, signUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}
