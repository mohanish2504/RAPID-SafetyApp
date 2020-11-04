package com.example.safetyapp.Triggers;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.safetyapp.Globals;
import com.example.safetyapp.user.ReferalActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TriggerData {
    private static List<String> Tokens = new ArrayList<String>();
    private static String TAG = TriggerData.class.getSimpleName();
    private Context context;

    public TriggerData() {

    }


    public void setData(final Context context){
        Log.d(TAG,"setting Data");
        this.context = context;
        final Date currentTime = Calendar.getInstance().getTime();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Triggers");

       // Globals.getPermissions(context,activity);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Globals.servicecontext);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location == null) {
                    Log.d(TAG, "NULL LOCATION");
                    Toast.makeText(context.getApplicationContext(), "Ferching Location Error", Toast.LENGTH_LONG).show();
                    return;
                }

                String mobile = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("Number", "");
                ArrayList<String> emergencyContacts = new ArrayList<>();
                String uri = "http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude();
                String mynum = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("Number", "");

                for(ReferalActivity.EmergencyContact e : Globals.emergencyContactslist){
                    emergencyContacts.add(e.getNumber());
                    sendSMS(mynum,uri,e.getNumber());
                }



                CurrentUserInfo currentInfo = new CurrentUserInfo(mobile, location, emergencyContacts, currentTime.toString());
                Log.d(TAG,Globals.MODE);
                if (Globals.MODE.equals("MODE | PUBLIC"))dbref.child(String.valueOf(System.currentTimeMillis())).setValue(currentInfo);
            }
        });

    }

    public void sendSMS(String phoneNo, String uri,String userphone) {
        String msg="Hello I am facing issues please help out! Here is my location " + uri;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userphone, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public class CurrentUserInfo {
        String Mobile;
        Location location;
        ArrayList<String> emergencyContacts ;
        String triggertime ;

        public CurrentUserInfo(String mobile, Location location, ArrayList<String> emergencyContacts, String triggertime) {
            Mobile = mobile;
            this.location = location;
            this.emergencyContacts = emergencyContacts;
            this.triggertime = triggertime;
        }

        public String getTriggertime() {
            return triggertime;
        }

        public void setTriggertime(String triggertime) {
            this.triggertime = triggertime;
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
