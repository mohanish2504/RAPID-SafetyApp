package com.example.safetyapp.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.Globals;
import com.example.safetyapp.R;
import com.example.safetyapp.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.safetyapp.Globals.userDetails;

public class signUpActivity extends AppCompatActivity {
    TextView dob;
    private static String TAG = signUpActivity.class.getSimpleName();
    DatePickerDialog.OnDateSetListener setListener;
    Button Submit;
    EditText fname,lname;
    AutoCompleteTextView autoCompleteTextView_city;
    RadioGroup radioGroup;
    int year,month,day;
    Calendar calendar;
    String selectedcity;
    ArrayList<String> listAll=new ArrayList<String>();
    int selectedfromlist = 0;
    TextView textView_ERROR;
    String firstname,lastname;
    RadioButton male,female;
    boolean status ;
    int ERROR;

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

        fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textView_ERROR == findViewById(R.id.fname_error)){
                    if(textView_ERROR.getVisibility() == View.VISIBLE){
                        textView_ERROR.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                firstname = s.toString();
                if(!firstname.matches("^[A-Za-z]+$") ){
                    textView_ERROR = findViewById(R.id.fname_error);
                    textView_ERROR.setText("only characters are allowed");
                    textView_ERROR.setVisibility(View.VISIBLE);
                }
            }
        });
        lname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textView_ERROR == findViewById(R.id.lname_error)){
                    if(textView_ERROR.getVisibility() == View.VISIBLE){
                        textView_ERROR.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                lastname = s.toString();
                if(!lastname.matches("^[A-Za-z]+$") ){
                    textView_ERROR = findViewById(R.id.lname_error);
                    textView_ERROR.setText("only characters are allowed");
                    textView_ERROR.setVisibility(View.VISIBLE);
                }
            }
        });

        uploadUserData();
        status = getSharedPreferences("SignUpDetails",MODE_PRIVATE).getBoolean("status",false);
        setUserData();
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAllValidations()) {
                    if(status){
                        Toast.makeText(getApplicationContext(),"Details Saved Succesfully",Toast.LENGTH_SHORT).show();
                        uploadUserData();
                    }else{




                        Intent intent = new Intent(getApplicationContext(), ReferalActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"ERROR Encountered!",Toast.LENGTH_LONG).show();
                }
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
            public void onDateSet(DatePicker view, int y, int m, int d) {
                calendar.set(y,m,d);
                m = m+1;
                year=y;month=m-1;day=d;
                String date = d+"-"+m+"-"+y;
                dob.setText(date);
            }
        };
        dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textView_ERROR == findViewById(R.id.dob_error)){
                    if(textView_ERROR.getVisibility() == View.VISIBLE){
                        textView_ERROR.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String date = s.toString();

                if(!checkAgeValidation()){
                    textView_ERROR = findViewById(R.id.dob_error);
                    textView_ERROR.setText("member must be above 13 years");
                    textView_ERROR.setVisibility(View.VISIBLE);
                    ERROR = 1;
                }else ERROR=0;
            }
        });

        radioGroup = findViewById(R.id.radgen);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(textView_ERROR == findViewById(R.id.gender_error)){
                    textView_ERROR.setVisibility(View.INVISIBLE);
                }
            }
        });
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
            InputStream is = getAssets().open("city.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listAll);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedcity = listAll.get(position);
                selectedfromlist = 1;
            }
        });
        textView_ERROR = (TextView) findViewById(R.id.city_error);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textView_ERROR == findViewById(R.id.city_error)){
                    if(textView_ERROR.getVisibility() == View.VISIBLE){
                        textView_ERROR.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedcity = s.toString();
                if(!listAll.contains(selectedcity)){
                    textView_ERROR = findViewById(R.id.city_error);
                    textView_ERROR.setText("City must be selected from the list");
                    textView_ERROR.setVisibility(View.VISIBLE);
                    ERROR = 1;
                }else{
                    ERROR = 0;
                }
            }
        });
    }



    private boolean checkAllValidations(){
        int validateionspassed = 0;

        if (checkFNameValidations())  validateionspassed++; else return false;
        if (checkLNameValidations())  validateionspassed++; else return false;
        if (checkDateValidations())   validateionspassed++; else return false;
        if (checkCityValidations())   validateionspassed++; else return false;
        if (checkGenderValidations()) validateionspassed++; else return false;

        if(validateionspassed==5){
            return true;
        }

        return false;

    }

    private boolean checkFNameValidations(){


        if(!firstname.isEmpty() && ERROR == 0){
            userDetails.setFirstName(firstname);
            getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("FirstName",firstname).apply();
            return true;
        }
        else if(textView_ERROR == findViewById(R.id.fname_error) || ERROR==1){
            return false;
        }
        textView_ERROR = (TextView) findViewById(R.id.fname_error);
        textView_ERROR.setVisibility(View.VISIBLE);
        textView_ERROR.setText("*this should not be Empty!");
        return false;
    }
    private boolean checkLNameValidations(){

        textView_ERROR = (TextView) findViewById(R.id.lname_error);

        if(!lastname.isEmpty() && ERROR==0){
            userDetails.setLastName(lastname);
            getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("LastName",lastname).apply();
            return true;
        }
        else if(textView_ERROR == findViewById(R.id.lname_error) || ERROR==1){
            return false;
        }
        textView_ERROR.setVisibility(View.VISIBLE);
        textView_ERROR.setText("*this should not be Empty!");

        return false;

    }
    private boolean checkGenderValidations(){
        int id = radioGroup.getCheckedRadioButtonId();
        textView_ERROR = findViewById(R.id.gender_error);
        if(id!=-1 && ERROR==0){
            String gen = "";
            if(id == R.id.radmale)gen = "Male";
            if(id == R.id.radfemale)gen = "Female";
            userDetails.setGender(gen);
            getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Gender",gen).apply();
            return true;
        }
        textView_ERROR.setText("Please select gender");
        textView_ERROR.setVisibility(View.VISIBLE);
        return false;
    }
    private boolean checkCityValidations(){
        autoCompleteTextView_city = findViewById(R.id.city);
        selectedcity = autoCompleteTextView_city.getText().toString();

        textView_ERROR = (TextView) findViewById(R.id.city_error);

        if(!selectedcity.isEmpty() && ERROR==0){
            userDetails.setCity(selectedcity);
            getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("City",selectedcity).apply();
            return true;
        }
        else if(textView_ERROR == findViewById(R.id.city_error) || ERROR==1){
            return false;
        }

        textView_ERROR.setVisibility(View.VISIBLE);
        textView_ERROR.setText("City must no be Empty!");

        return false;
    }
    private boolean checkDateValidations(){
        if(!dob.getText().toString().isEmpty() && ERROR==0){
            userDetails.setDOB(dob.getText().toString());
            getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("DOB",dob.getText().toString()).apply();
            return true;
        }
        else if(textView_ERROR == findViewById(R.id.dob_error) || ERROR==1){
            return false;
        }
        textView_ERROR = findViewById(R.id.dob_error);
        textView_ERROR.setText("memeber must be above 13 years");
        textView_ERROR.setVisibility(View.VISIBLE);
        return false;
    }
    private boolean checkAgeValidation(){
        Date date = calendar.getTime();
        Date currentdate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-=mm-yyyy");
        Log.d(TAG,String.valueOf(date) + " " + String.valueOf(currentdate));
        long diff  = currentdate.getTime() - date.getTime();
        long years = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)/365;

        if(years>12){
            return true;
        }

        return false;
    }



    public void setUserData(){
        if(!status)return;

      //  Log.d("Data",userDetails.getFirstName());

        fname.setText(userDetails.getFirstName());
        lname.setText(userDetails.getLastName());
        autoCompleteTextView_city = findViewById(R.id.city);
        autoCompleteTextView_city.setText(userDetails.getCity());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try{
            sdf.parse(userDetails.getDOB());
            calendar = sdf.getCalendar();

            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH) + 1;
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            String date = d + "-" + m + "-" + y;

            year=y;month=m-1;day=d;
            calendar.set(y,m,d);
            dob.setText(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        String gender = userDetails.getGender();
        male = findViewById(R.id.radmale);
        female = findViewById(R.id.radfemale);
        if(gender.equals("Male"))male.setChecked(true);
        else female.setChecked(true);
    }
    private void uploadUserData() {
        final String UID = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("UserDetails");
        databaseReference.child(UID).setValue(userDetails);
    }
}
