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
import java.util.Map;

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

    public void setData(final Context context){
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
                String mobile = context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("Number","");

                int ec_sz = context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getInt("EC_SIZE",0);
                ArrayList<String> emergencyContacts= new ArrayList<>();

                String title = "EmergencyContact";
                for(int j = 0;j<ec_sz;j++){
                    emergencyContacts.add(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString(title+String.valueOf(j+1),""));
                }

                CurrentUserInfo currentInfo = new CurrentUserInfo(mobile,location,emergencyContacts);

                dbref.child(String.valueOf(currentTime)).setValue(currentInfo);
            }
        });

        //return Tokens;
    }

    public class CurrentUserInfo {
        String Mobile;
        Location location;
        ArrayList<String> emergencyContacts ;

        public CurrentUserInfo(String mobile, Location location, ArrayList<String> emergencyContacts) {
            Mobile = mobile;
            this.location = location;
            this.emergencyContacts = emergencyContacts;
        }

        public CurrentUserInfo() {
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String mobile) {
            Mobile = mobile;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public ArrayList<String> getEmergencyContacts() {
            return emergencyContacts;
        }

        public void setEmergencyContacts(ArrayList<String> emergencyContacts) {
            this.emergencyContacts = emergencyContacts;
        }
    }
}
