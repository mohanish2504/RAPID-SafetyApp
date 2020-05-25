package com.example.safetyapp.intro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.example.safetyapp.user.ReferalActivity;
import com.example.safetyapp.user.phoneno;

public class FrontActivity extends AppCompatActivity {
    private static String TAG = FrontActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        if(!isNetworkAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("No Internet");
            builder.setMessage("Sorry no internet connection detected. Please connect with internet and try again");
            builder.setCancelable(false);
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    overridePendingTransition(0, 0);
                    recreate();
                    overridePendingTransition(0, 0);
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            checkRequiredValidations();
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkRequiredValidations(){
        boolean loginstatus = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getBoolean("Status",false);
        boolean contactsVerified = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getBoolean("ContactsVerification",false);
        final Intent intent;
        Log.d(TAG,Boolean.toString(loginstatus));
        if(loginstatus && contactsVerified){
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }else if(!loginstatus){
            intent = new Intent(getApplicationContext(), HighlightsIntro.class);
        }else{
            Toast.makeText(this,"PLease Add atleast 1 contact",Toast.LENGTH_SHORT).show();
            intent = new Intent(getApplicationContext(), ReferalActivity.class);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        },1000);



    }
}
