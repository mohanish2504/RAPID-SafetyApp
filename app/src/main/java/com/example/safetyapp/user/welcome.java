package com.example.safetyapp.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.example.safetyapp.R;

import com.example.safetyapp.ReferalGenerator;
import com.example.safetyapp.intro.IntroAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class welcome extends AppCompatActivity {

    ViewPager viewPager;
    private Button button;

    private static String TAG = welcome.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.viewpager);

        IntroAdapter adapter = new IntroAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    public void getPermissions(){


        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = null;
                intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
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


    }

}
