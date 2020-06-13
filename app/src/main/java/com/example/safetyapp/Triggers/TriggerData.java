package com.example.safetyapp.Triggers;

import android.content.Context;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.safetyapp.Globals;
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
        this.context = context;
        Log.d(TAG,"setting Data");

        final Date currentTime = Calendar.getInstance().getTime();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Triggers");


        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try{
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String mobile = context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("Number","");
                ArrayList<String> emergencyContacts= new ArrayList<>();
                String uri = "http://maps.google.com/maps?saddr=" +location.getLatitude()+","+location.getLongitude();
                String mynum = context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("Number","");
                for(int j = 0;j<Globals.emergencyContactslist.size();j++){
                    emergencyContacts.add(Globals.emergencyContactslist.get(j).getNumber());
                    sendSMS(mynum,uri,Globals.emergencyContactslist.get(j).getNumber());
                }

                CurrentUserInfo currentInfo = new CurrentUserInfo(mobile,location,emergencyContacts,currentTime.toString());

                if(Globals.MODE.equals("MODE | PUBLIC"))dbref.child(String.valueOf(System.currentTimeMillis())).setValue(currentInfo);
            }
        });
        }catch (Exception e){
           Toast.makeText(context,"Location Permissions are not allowed",Toast.LENGTH_SHORT).show();
        }

    }

    public void sendSMS(String phoneNo, String uri,String userphone) {
        String msg="Hello I am facing issues pls help out! Here is my location " + uri;
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
