package com.example.safetyapp.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.example.safetyapp.user.ReferalActivity;
import com.example.safetyapp.user.phoneno;
import com.example.safetyapp.user.signUpActivity;

public class SecoundFragment extends Fragment {
    private static final String TAG = SecoundFragment.class.getSimpleName();
    public SecoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.secoundslider, container, false);


        final Intent intent = new Intent(getActivity(), phoneno.class);
        final Intent SignUpIntent =  new Intent(getActivity(), signUpActivity.class);

        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRequiredValidations();
            }
        });
        return  view;
    }

    public void checkRequiredValidations(){
        boolean loginstatus = getActivity().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getBoolean("Status",false);
        boolean contactsVerified = getActivity().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getBoolean("ContactsVerification",false);
        Intent intent;
        Log.d(TAG,Boolean.toString(loginstatus));
        if(loginstatus && contactsVerified){
            intent = new Intent(getContext(), MainActivity.class);
        }else if(!loginstatus){
            intent = new Intent(getContext(), phoneno.class);
        }else{
            intent = new Intent(getContext(), ReferalActivity.class);
        }
        startActivity(intent);
    }
}
