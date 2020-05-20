package com.example.safetyapp.user;

import android.content.BroadcastReceiver;
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

import com.example.safetyapp.Globals;
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
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import static com.example.safetyapp.R.id.frag_map;
import static java.util.Objects.*;

public class portal extends AppCompatActivity {

    private static String TAG = portal.class.getSimpleName();
    public static ListView listView ;
    public static ListViewAdapter listViewAdapter;

    public void init(){
        listView = findViewById(R.id.listview_helps);
        listViewAdapter = new ListViewAdapter(this,R.layout.helprequest_card);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        Globals.pendingrequests = 0;
        init();
        listView.setAdapter(listViewAdapter);
        //listViewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class ListViewAdapter extends ArrayAdapter<EmergencyContact> implements OnMapReadyCallback {

        ArrayList<HelpRequests.UserInNeed> userInNeedArrayAdapter;
        GoogleMap gmap;
        int currposition;

        public ListViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            Map<Long, HelpRequests.UserInNeed> helprequests = HelpRequests.getUsers();

            userInNeedArrayAdapter = new ArrayList<>();
            Log.d(TAG, String.valueOf(helprequests.size()) +" " + String.valueOf(HelpRequests.currentRequests()));
            userInNeedArrayAdapter.addAll(helprequests.values());
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

            //lkjlnom
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
