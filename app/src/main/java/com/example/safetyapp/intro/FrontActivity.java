package com.example.safetyapp.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.example.safetyapp.user.ReferalActivity;

public class FrontActivity extends AppCompatActivity {
    private static String TAG = FrontActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        checkRequiredValidations();


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
        },1500);



    }
}
