package com.example.safetyapp.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.safetyapp.R;
import com.example.safetyapp.SharedPrefs;

public class welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btn_signin=(Button)findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.saveSharedSetting(welcome.this, "Safety", "true");
                Intent i=new Intent(welcome.this, signin.class);
                startActivity(i);
            }
        });

        Button btn_signup=(Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(welcome.this, signup.class);
                startActivity(i);
            }
        });
        CCSession();
    }
    public void CCSession(){

        Boolean Check = Boolean.valueOf(SharedPrefs.readSharedSetting(welcome.this, "Safety", "false"));

        Intent introIntent = new Intent(welcome.this, signin.class);
        introIntent.putExtra("Safety", Check);
        if (Check) {
            startActivity(introIntent);
            finish();
        }
    }
}
