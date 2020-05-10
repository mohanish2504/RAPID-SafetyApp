package com.example.safetyapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.example.safetyapp.ReferalGenerator;
import com.example.safetyapp.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class verify_phone extends AppCompatActivity {

    private OtpView editTextcode;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private String codeSend;
    String mobile;
    private static final String TAG = AppCompatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        editTextcode = (OtpView) findViewById(R.id.otp);
        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Number",mobile).apply();
        String countrycode = intent.getStringExtra("countrycode");

        sendVerificationCode(mobile,countrycode);

        findViewById(R.id.sign__in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code =  editTextcode.getText().toString().trim();
                if(code.isEmpty() || code.length() < 6){
                    editTextcode.setError("Enter valid code");
                    editTextcode.requestFocus();
                    return;
                }
                verifyVerificationCode(code);
            }
        });
    }
    private void sendVerificationCode(final String mobile, String countrycode){

         PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();



                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                if(code != null){
                    editTextcode.setText(code);
                    verifyVerificationCode(code);
                }
                //ReferalGenerator.checkForReferal(FirebaseAuth.getInstance().getCurrentUser().toString());
                Log.d("OnVerify","completed");
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(verify_phone.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
            }
        };

        Log.d(TAG,countrycode+mobile);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countrycode + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private void verifyVerificationCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(verify_phone.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                           // ReferalGenerator.checkForReferal(mobile);

                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                           // Intent intent = new Intent(getApplicationContext(),ReferalActivity.class);
                            //startActivity(intent);
                            getSharedPreferences("LoginDetails",MODE_PRIVATE).edit().putBoolean("Status",true).apply();
                            if(isNewUser){
                                Intent intent = new Intent(verify_phone.this, signUpActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(verify_phone.this, ReferalActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            //getSharedPreferences("Info",MODE_PRIVATE).edit().putBoolean("LoginStatus",true).apply();
                            //getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Number",mobile).apply();
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

}