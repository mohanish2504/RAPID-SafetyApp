package com.example.safetyapp.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.safetyapp.R;
import com.example.safetyapp.ReferalGenerator;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class phoneno extends AppCompatActivity {

    CountryCodePicker ccp;
    private EditText editTextMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneno);

        editTextMobile = (EditText) findViewById(R.id.phone_no);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String countrycode = ccp.getSelectedCountryCodeWithPlus();
                String mobile = editTextMobile.getText().toString();


                if(mobile.isEmpty() || mobile.length() < 10){
                    editTextMobile.setError("Enter Vaild Number");
                    editTextMobile.requestFocus();
                    return;
                }

                Intent intent = new Intent(phoneno.this, verify_phone.class);

                intent.putExtra("mobile", mobile);
                intent.putExtra("countrycode" , countrycode);
                startActivity(intent);
                finish();
            }
        });
    }
}
