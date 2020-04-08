package com.example.safetyapp.Triggers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.safetyapp.Services.Service;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Trigger {
    private static String TAG = Trigger.class.getSimpleName();
    private static DatabaseReference databaseReference;
    private static String androidId;
    private static TriggerData trigger;

    private void setReference(Context context){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Triggers");
        //Log.d(TAG,"here");
        androidId="A11";
        trigger = new TriggerData();
        trigger.setData(context);


    }

    public void registerTrigger(Context context){
        setReference(context);
        registerGeoFire();
    }

    private void registerGeoFire(){
        Location currentUserLocation = Service.locationUser;
        GeoFire geoFire = Service.geoFireTrigger;
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentUserLocation.getLatitude(),currentUserLocation.getLongitude()),3);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.d(TAG, String.valueOf(dataSnapshot.child("l").child("0")+" "+dataSnapshot.child("l").child("1")));

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }



}
