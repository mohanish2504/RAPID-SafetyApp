package com.example.safetyapp.intro;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.safetyapp.R;
import com.example.safetyapp.user.phoneno;

import java.security.AllPermission;

import static com.example.safetyapp.user.EmergencyContact.RequestPermissionCode;
public class AlertAndPermissions extends AppCompatActivity {

    private static String TAG = AllPermission.class.getSimpleName();

    private int REQUEST_CODE = 1;
    private int requestDND = 0;


    String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fourth);

        Button button_allowpermissions = findViewById(R.id.allow_permission);
        button_allowpermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissions();
            }
        });

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void getPermissions(){
        //requestDNDPermissions();
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE);
        }


        /*requestLocationPermission();
        requestMicrophonePermission();
        requestContactsAndSMSPermission();


        Intent intent = new Intent(this, phoneno.class);
        //startActivity(intent);*/
    }

    public void requestDNDPermissions(){

        NotificationManager mNotificationManager = (NotificationManager)  this.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Toast.makeText(this,R.string.ALERTANDPERMISSION_TOAST_DNDPERMISSION,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                requestDND = 1;
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            Intent intent = new Intent(this,phoneno.class);
            startActivity(intent);
            requestDNDPermissions();
        }
    }


    private void handleLocationUpdates() {
        //foreground and background
        //Toast.makeText(getApplicationContext(),"Start Foreground and Background Location Updates",Toast.LENGTH_SHORT).show();
    }

    private void handleForegroundLocationUpdates() {
        //handleForeground Location Updates
        //Toast.makeText(getApplicationContext(),"Start foreground location updates",Toast.LENGTH_SHORT).show();
    }
}

