package com.example.safetyapp.user;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.R;

public class ReferalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Note: Please send link to contacts and install the app in your \n" +
                "emergency contacts' device to get referral code ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }
}
