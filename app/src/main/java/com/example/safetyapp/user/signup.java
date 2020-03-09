package com.example.safetyapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    EditText Name,Email,Password;
    Button btnSignUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mAuth=FirebaseAuth.getInstance();
        Name=findViewById(R.id.name);
        Email=findViewById(R.id.email);
        Password=findViewById(R.id.password);
        btnSignUp=findViewById(R.id.sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=Name.getText().toString();
                String email=Email.getText().toString();
                String pwd=Password.getText().toString();
                if(name.isEmpty()){
                    Name.setError("Enter Your Email");
                    Name.requestFocus();
                }
                else if(email.isEmpty()){
                    Email.setError("Please Enter Your E-Mail");
                    Email.requestFocus();
                }
                else if(pwd.isEmpty()){
                    Password.setError("Please Enter Password");
                    Password.requestFocus();
                }
                else if(name.isEmpty() && email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(signup.this,"Please Fill Sign Up Fileds",Toast.LENGTH_SHORT).show();
                    Toast.makeText(signup.this,"LOL--:)",Toast.LENGTH_SHORT).show();

                }
                else if(!(name.isEmpty() && email.isEmpty() && pwd.isEmpty())){
                    mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(signup.this,"SignUp UnSucessfull",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                startActivity(new Intent(signup.this,MainActivity.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(signup.this,"Error While SignUp Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
