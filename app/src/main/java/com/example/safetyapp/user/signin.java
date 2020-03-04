package com.example.safetyapp.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;

import org.w3c.dom.Text;

public class signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        final Button signin=(Button)findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(signin.this, MainActivity.class);
                startActivity(i);
            }
        });

        TextView forgetpasstext=(TextView)findViewById(R.id.forgetpasstext);
        forgetpasstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(signin.this,forget_password.class);
                startActivity(i);
            }
        });
    }
}
