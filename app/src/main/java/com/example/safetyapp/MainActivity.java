package com.example.safetyapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.safetyapp.Firebase.SendData;
import com.example.safetyapp.restarter.RestartServiceBroadcastReceiver;
import com.example.safetyapp.screenreceiver.ScreenOnOffReceiver;
import com.example.safetyapp.user.portal;
import com.example.safetyapp.user.profile;
import com.example.safetyapp.user.welcome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ScreenOnOffReceiver screenOnOffReceiver = null;
    private static String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int click_counter;
    long totalTime;
    TextView textView;
    private static String channelID = "Notification Channel";
    private static String Token;
    DatabaseReference databaseReference;
    SendData sendData;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    MenuItem btnlogout;
    private FirebaseAuth mAuth;
    private static final int PERMISSIONS_REQUEST = 1;
    ImageView user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_img = findViewById(R.id.user_img);


        Button btnportal = findViewById(R.id.btnportal);
        btnportal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, portal.class);
                startActivity(i);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String email = user.getEmail();
            TextView textView = (TextView) findViewById(R.id.nav_email);
            textView.setText(email);

            if(user.getPhotoUrl() != null){
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into((ImageView)user_img );
            }
        }


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            //finish();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);


        } else {
            // already permission granted
        }


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
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


        sharedPref = getApplicationContext().getSharedPreferences("Counter", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        sendData = new SendData();

        checkCounter();

        setToken();

        createMethod();

        scheduleJob();


        /*FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        //Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy()","Destroyed");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Log.d("onDestroy()","Starting Process");
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }
    //@Override
    protected void onResume() {
        super.onResume();
    }*/

    private void checkCounter() {
       /* if(sharedPref.contains("click_counter")){
            click_counter = sharedPref.getInt("click_counter",0);
            if(click_counter >= 10){
                editor.putInt("click_counter",0);
                totalTime = sharedPref.getLong("totalTime",0);
                editor.putLong("totalTime",0);
                editor.commit();
                Log.d("AverageTime",Long.toString(totalTime/10));
                textView.setText(Long.toString(totalTime/10));
            }
        }*/
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
                        Token = token;
                        sendData.sendToken("", token);

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
        else if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this,welcome.class));
        }
        return false;
    }
}