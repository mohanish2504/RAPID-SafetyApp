package com.example.safetyapp.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class signin extends AppCompatActivity {

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="signin";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    EditText EmailId,Password;
    Button SignIn;
    TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signInButton=findViewById(R.id.button_google_signin);
        mAuth=FirebaseAuth.getInstance();
        EmailId=findViewById(R.id.userid);
        Password=findViewById(R.id.pass);
        SignIn=findViewById(R.id.signin);
        tvSignUp=findViewById(R.id.forgetpasstext);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
                if(mFirebaseUser!=null){
                    Toast.makeText(signin.this,"You Are Logged In",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(signin.this,MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(signin.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=EmailId.getText().toString();
                String pwd=Password.getText().toString();
                if(email.isEmpty()){
                    EmailId.setError("Please Enter Email ID");
                    EmailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    Password.setError("Please Enter Password");
                    Password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(signin.this,"Filled Is Empty",Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    Log.d("ERROR","HERE");
                    mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(signin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(signin.this,"Login Error...--)",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent inToHome=new Intent(signin.this,MainActivity.class);
                                startActivity(inToHome);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(signin.this,"Error...--)",Toast.LENGTH_SHORT).show();

                }

            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(signin.this,signup.class);
                startActivity(i);
            }
        });


        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

    }

    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void SignIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try{
            GoogleSignInAccount acc=completedTask.getResult(ApiException.class);
            Toast.makeText(signin.this,"Sign In Sucessfull",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(signin.this,"Sign In Failed",Toast.LENGTH_SHORT).show();

        }
    }

    private  void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(signin.this,"Sucessfull",Toast.LENGTH_SHORT).show();
                    FirebaseUser user=mAuth.getCurrentUser();
                    updateUI(user);
                    Intent i=new Intent(signin.this,MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(signin.this,"UnSucessfull",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null){
            String personName = account.getDisplayName();
            String personGivenName=account.getGivenName();
            String personFamilyName=account.getFamilyName();
            String personEmail=account.getEmail();
            String personId=account.getId();
            Uri personPhoto=account.getPhotoUrl();

            Toast.makeText(signin.this,personName + personEmail,Toast.LENGTH_SHORT).show();
        }
    }
}
