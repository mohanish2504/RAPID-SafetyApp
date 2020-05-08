package com.example.safetyapp.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;

import java.util.Calendar;

public class signUpActivity extends AppCompatActivity {
    TextView dob;
    DatePickerDialog.OnDateSetListener setListener;
    Button Submit;
    EditText fname;
    EditText lname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dob = findViewById(R.id.dob);
        Submit =  (Button) findViewById(R.id.SubmitButton);
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("FirstName", String.valueOf(fname.getText())).apply();
                getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("LastName","lastName").apply();
                getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Gender","gender").apply();
                getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("City","city").apply();
                getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("DOB","dob").apply();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        signUpActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                dob.setText(date);
            }
        };
    }
}
