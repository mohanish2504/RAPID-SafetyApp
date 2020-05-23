package com.example.safetyapp.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import org.json.JSONObject;

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
    EditText fname,lname;
    RadioGroup radioGroup;
    int year,month,day;
    Calendar calendar;
    String selectedcity;
    ArrayList<String> listAll=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dob = findViewById(R.id.dob);
        radioGroup = findViewById(R.id.radgen);
        Submit =  (Button) findViewById(R.id.SubmitButton);
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);



        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllValidations();
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

        init();
    }

    public void init(){
        obj_list();
        addToATC();
    }

    public String getJson() {
        String json=null;
        try
        {
            // Opening cities.json file
            InputStream is = getAssets().open("city.json");
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    public void obj_list() {
        try
        {
            JSONObject jsonObject=new JSONObject(getJson());
            JSONArray array=jsonObject.getJSONArray("array");
            for(int i=0;i<array.length();i++)
            {
                JSONObject object=array.getJSONObject(i);
                String city=object.getString("name");
                String state=object.getString("state");

                listAll.add(city+" , "+state);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void addToATC() {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.city);
        // Adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listAll);
        // adapter.setDropDownViewResource(android.R.layout.);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedcity = listAll.get(position);
            }
        });
    }



    private boolean checkAllValidations(){
        return checkFNameValidations(fname.getText().toString()) &&
        checkLNameValidations(lname.getText().toString()) &&
        checkGenderValidations() &&
        checkCityValidations() &&
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
    private boolean checkCityValidations(){
        if(selectedcity!=null)return true;
        return false;
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
