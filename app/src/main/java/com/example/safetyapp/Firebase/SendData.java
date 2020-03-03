package com.example.safetyapp.Firebase;

import android.provider.ContactsContract;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendData {
    DatabaseReference databaseReference;

    public void sendToken(String url , String token){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Tokens/"+token).setValue(true);

    }

}
