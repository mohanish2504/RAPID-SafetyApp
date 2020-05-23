package com.example.safetyapp.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class signUpActivity extends AppCompatActivity {
    TextView dob;
    private static String TAG = signUpActivity.class.getSimpleName();
    DatePickerDialog.OnDateSetListener setListener;
    Button Submit;
    EditText fname;
    EditText lname;
    Spinner city;
    RadioGroup radioGroup;
    int year,month,day;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dob = findViewById(R.id.dob);
        radioGroup = findViewById(R.id.radgen);
        Submit =  (Button) findViewById(R.id.SubmitButton);
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        city = (Spinner) findViewById(R.id.city);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Spinner city = (Spinner) findViewById(R.id.city);
        ArrayList<String> items = getCities("city.json");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this ,R.layout.spinner_layout, R.id.city);
        city.setAdapter(adapter);


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllValidations();
                //getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("FirstName", String.valueOf(fname.getText())).apply();
               // getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("LastName","lastName").apply();
               // getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Gender","gender").apply();
               // getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("City","city").apply();
                //getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("DOB","dob").apply();
                Intent intent = new Intent(getApplicationContext(), ReferalActivity.class);
                //startActivity(intent);
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
                calendar.set(year,month,dayOfMonth);
                month = month+1;
                String date = day+"/"+month+"/"+year;
                dob.setText(date);
            }
        };
    }

    public ArrayList<String> getCities(String fileName){
        JSONArray jsonArray  = null;
        ArrayList<String> cList = new ArrayList<String>();
        try {
            InputStream inputStream = getResources().getAssets().open(fileName);
            int size = inputStream.available();
            byte[]  data = new byte[size];
            inputStream.read(data);
            String json = new String(data, "UTF-8");
            jsonArray = new JSONArray(json);
            if(jsonArray!=null){
                for(int i=0; i<json.length(); i++){
                    cList.add(jsonArray.getJSONObject(i).getString("cname"));
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException je){
            je.printStackTrace();
        }
        return cList;
    }

    private boolean checkAllValidations(){
        return checkFNameValidations(fname.getText().toString()) &&
        checkLNameValidations(lname.getText().toString()) &&
        checkGenderValidations() &&
        //checkCityValidations(city.getText().toString()) &&
        checkAgeValidations(year);

    }
    private boolean checkFNameValidations(String name){
        if(!name.matches("^[A-Za-z]+$") || name.isEmpty()){
            Log.d(TAG,"Error FirstName");
            return false;
        }
        return true;
    }
    private boolean checkLNameValidations(String name){
        if(!name.matches("^[A-Za-z]+$") || name.isEmpty()){
            Log.d(TAG,"Error LastName");
            return false;
        }
        return true;
    }
    private boolean checkGenderValidations(){
        int id = radioGroup.getCheckedRadioButtonId();
        if(id==-1)return false;
        return true;
    }
    private boolean checkCityValidations(String city){
        return true;
    }
    private boolean checkAgeValidations(int year){
        Date date = calendar.getTime();
        Date currentdate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-=mm-yyyy");
        Log.d(TAG,String.valueOf(date) + " " + String.valueOf(currentdate));
        long diff  = currentdate.getTime() - date.getTime();
        long years = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)/365;

        if(years>12)return true;

        return true;

        //Log.d(TAG,String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)/365));

    }
}
