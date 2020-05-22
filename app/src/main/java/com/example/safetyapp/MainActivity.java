package com.example.safetyapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.safetyapp.Firebase.SendData;
import com.example.safetyapp.Services.RingtonePlayingService;
import com.example.safetyapp.restarter.RestartServiceBroadcastReceiver;
import com.example.safetyapp.screenreceiver.ScreenOnOffReceiver;
import com.example.safetyapp.user.ReferalActivity;
import com.example.safetyapp.user.phoneno;
import com.example.safetyapp.user.portal;
import com.example.safetyapp.user.profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ScreenOnOffReceiver screenOnOffReceiver = null;
    private static String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private static String channelID = "Notification Channel";
    private static String Token;
    DatabaseReference databaseReference;
    SendData sendData;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    MenuItem btnlogout;
    private FirebaseAuth mAuth;
    private String UID;

    private static UserDetails userDetails;
    FirebaseDatabase firebaseDatabase;
    Intent sirenIntent;
    int helprequests;
    Button btnportal,btnsafetystatus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //helprequests = getSharedPreferences("HelpRequests",MODE_PRIVATE).getInt("current_help_requests",HelpRequests.currentRequests());


        final Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        overridePendingTransition(0, 0);

        sirenIntent = new Intent(getApplicationContext(), RingtonePlayingService.class);
        btnsafetystatus = (Button) findViewById(R.id.safe);


        final String safetystatus = getSharedPreferences("Info",MODE_PRIVATE).getString("SafetyStatus","ON");
        Log.d(TAG,safetystatus);

        if(safetystatus.equals("OFF")) {
            btnsafetystatus.setBackgroundResource(R.drawable.safe_new);
            btnsafetystatus.setText("Safe");
            //btnsafetystatus.setBackgroundColor(Color.parseColor("#FF0000"));
        }else if(safetystatus.equals("ON")){
            btnsafetystatus.setBackgroundResource(R.drawable.unsafe_new);
            btnsafetystatus.setText("Unsafe");
            //btnsafetystatus.setBackgroundColor(Color.parseColor("#008000"));
        }

        btnsafetystatus.setOnClickListener(new View.OnClickListener() {
            String str = safetystatus;
            @Override
            public void onClick(View v) {
                Log.d(TAG,str);
                if(str.equals("OFF")) {
                    Log.d(TAG,"OFF to ON");
                    stopService(sirenIntent);
                    getSharedPreferences("Info",MODE_PRIVATE).edit().putString("SafetyStatus","ON").apply();
                    str = "ON";
                    btnsafetystatus.setBackgroundResource(R.drawable.safe_new);
                    btnsafetystatus.setText("Safe");

                    //btnsafetystatus.setBackgroundColor(getResources().getColor(R.color.green));
                }else if(str.equals("ON")){
                    Log.d(TAG,"ON to OFF");
                    startService(sirenIntent);
                    getSharedPreferences("Info",MODE_PRIVATE).edit().putString("SafetyStatus","OFF").apply();
                    str = "OFF";
                    btnsafetystatus.setBackgroundResource(R.drawable.unsafe_new);
                    btnsafetystatus.setText("Unsafe");
                    //btnsafetystatus.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
        });


       btnportal = findViewById(R.id.portal);
       btnportal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, portal.class);
                startActivity(i);
            }
        });



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid().toString();
        if(user != null){
            String email = user.getPhoneNumber();
            NavigationView navigationView = findViewById(R.id.nav_view);
            View hView = navigationView.getHeaderView(0);

        }

        mAuth=FirebaseAuth.getInstance();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        btnlogout =(MenuItem) findViewById(R.id.logout);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       /*sendData = new SendData();

       ReferalGenerator.checkForReferal(UID);

        createMethod();

        scheduleJob();

        setUserData(getSharedPreferences("UserDetails",MODE_PRIVATE).getAll());

        uploadUserData();*/


        // New Modifications Comment this
        UID = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");
        ReferalGenerator.checkForReferal(UID);
        setToken();
        createMethod();
        scheduleJob();
        setUserData(getSharedPreferences("UserDetails",MODE_PRIVATE).getAll());
        uploadUserData();

    }

    private void uploadUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("UserDetails");
        //Log.d("UID",UID);
        databaseReference.orderByKey().equalTo(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else
                {
                    databaseReference.child(UID).setValue(userDetails);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    void setUserData(Map userdetails_map){
        userDetails = new UserDetails(userdetails_map);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Token",token).apply();
                        DatabaseReference mdatabaseReference;
                        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
                        String mobile = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");
                        mdatabaseReference.child(mobile).setValue(token);

                    }
                });
    }

    private void createMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Project";
            String description = "new message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void scheduleJob() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Log.d("MainActivity","Starting Process");
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            Log.d(TAG,"i m here");
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.editprofile) {
            Intent i = new Intent(MainActivity.this,profile.class);
            startActivity(i);
        }
        else if(id == R.id.emergencies_contacts){
            Log.d("You are here","hello");
            Intent i = new Intent(MainActivity.this, ReferalActivity.class);
            startActivity(i);
        }
        else if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            getSharedPreferences("Info",MODE_PRIVATE).edit().putBoolean("LoginStatus",false).apply();
            finish();
            startActivity(new Intent(this, phoneno.class));
        }
        return false;
    }

}