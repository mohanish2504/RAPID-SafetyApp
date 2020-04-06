package com.example.safetyapp.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.safetyapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class portal extends FragmentActivity implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_map);
        mapFragment.getMapAsync(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String email = user.getEmail();
            TextView textView = findViewById(R.id.frag_user_email);
            textView.setText(email);
        }


        Button btn_navigate = findViewById(R.id.frag_map_navi);
        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("geo:23.0225, 72.5714"));
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng Ahmedabad = new LatLng(23.0225, 72.5714);
        googleMap.addMarker(new MarkerOptions().position(Ahmedabad).title("Ahmedabad"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Ahmedabad));
    }
}
