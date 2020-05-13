package com.example.safetyapp.user;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.safetyapp.HelpRequests;
import com.example.safetyapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Stack;

import static com.example.safetyapp.R.id.frag_map;
import static java.util.Objects.*;

public class portal extends AppCompatActivity {

    private static String TAG = portal.class.getSimpleName();
    ListView listView;
    ListViewAdapter listViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        listView = findViewById(R.id.listview_helps);
        listViewAdapter = new ListViewAdapter(this,R.layout.helprequest_card);

        listView.setAdapter(listViewAdapter);

        /*TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Anjitha has requested you for help!\n" +
                "Please share your contact details and\n" +
                "use location to reach out before any \n" +
                "incident happens ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

        TextView datetime = (TextView) findViewById(R.id.dandt);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        datetime.setText(currentDateTimeString);

        imageView = findViewById(R.id.more);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.report_menu, popupMenu.getMenu());

                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.report:
                                Toast.makeText(portal.this, "Report Selected" , Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_map);
        mapFragment.getMapAsync(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String email = user.getPhoneNumber();
            TextView textView = findViewById(R.id.user_name);
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
        });*/
    }

    public class ListViewAdapter extends ArrayAdapter<EmergencyContact> implements OnMapReadyCallback {

        ArrayList<HelpRequests.UserInNeed> userInNeedArrayAdapter;
        GoogleMap gmap;
        int currposition;

        public ListViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            ArrayList<HelpRequests.UserInNeed> helprequests = HelpRequests.getUsers();

            userInNeedArrayAdapter = new ArrayList<>();
            Log.d(TAG, String.valueOf(helprequests.size()) +" " + String.valueOf(HelpRequests.currentRequests()));
            for(int i = 0 ; i<helprequests.size();i++){
                userInNeedArrayAdapter.add(helprequests.get(i));
            }
        }

        @Override
        public int getCount() {
           // Log.d(TAG, String.valueOf(userInNeedArrayAdapter.size()));
            return userInNeedArrayAdapter.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.helprequest_card, parent, false);
            }

            currposition = position;
            TextView textView = convertView.findViewById(R.id.user_name);
            textView.setText(userInNeedArrayAdapter.get(position).getFirstName());

            MapView mapView = convertView.findViewById(frag_map);

            if(mapView!=null){
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(getNewOnReadyCallBack(position));
            }

            return convertView;
        }



        @Override
        public void onMapReady(GoogleMap googleMap) {

        }

       /* public void setMap(){

            Double lat,lon;
            lat = Double.parseDouble(userInNeedArrayAdapter.get(currposition).getLat());
            lon = Double.parseDouble(userInNeedArrayAdapter.get(currposition).getLon());

            LatLng location = new LatLng(lat,lon);

            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f));
            gmap.addMarker(new MarkerOptions().position(location));

            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }*/

        OnMapReadyCallback getNewOnReadyCallBack(final int position){
            return new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MapsInitializer.initialize(getApplicationContext());
                    Double lat,lon;
                    lat = Double.parseDouble(userInNeedArrayAdapter.get(position).getLat());
                    lon = Double.parseDouble(userInNeedArrayAdapter.get(position).getLon());
                    LatLng location = new LatLng(lat,lon);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f));
                    googleMap.addMarker(new MarkerOptions().position(location));

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    //setMap();
                }
            };
        }


    }



}
