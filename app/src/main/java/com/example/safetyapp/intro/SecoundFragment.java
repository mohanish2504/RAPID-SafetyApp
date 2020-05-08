package com.example.safetyapp.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.safetyapp.R;
import com.example.safetyapp.user.ReferalActivity;
import com.example.safetyapp.user.phoneno;
import com.example.safetyapp.user.signUpActivity;

public class SecoundFragment extends Fragment {

    public SecoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_secound, container, false);


        final Intent intent = new Intent(getActivity(), phoneno.class);
        final Intent SignUpIntent =  new Intent(getActivity(), signUpActivity.class);

        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        return  view;
    }
}
