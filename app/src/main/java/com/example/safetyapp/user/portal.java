package com.example.safetyapp.user;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.safetyapp.Globals;
import com.example.safetyapp.HelpRequests;
import com.example.safetyapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.safetyapp.R.id.design_bottom_sheet;
import static com.example.safetyapp.R.id.emergencies_contacts;
import static com.example.safetyapp.R.id.frag_map;


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

        init();
        listView.setAdapter(listViewAdapter);

        Globals.pendingrequests=0;
        //listViewAdapter.notifyDataSetChanged();

    }

    public class ListViewAdapter extends ArrayAdapter<EmergencyContact> implements OnMapReadyCallback{

        ArrayList<HelpRequests.UserInNeed> userInNeedArrayList;
        GoogleMap gmap;
        int currposition;

        final String[] list = getContext().getResources().getStringArray(R.array.report_reason);
        final boolean[] result = new boolean[list.length];
        ArrayList<String> reports = new ArrayList<>();

        public ListViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            Map<Long, HelpRequests.UserInNeed> helprequests = HelpRequests.getUsers();

            userInNeedArrayList = new ArrayList<>();
            Log.d(TAG, String.valueOf(helprequests.size()) +" " + String.valueOf(HelpRequests.currentRequests()));
            userInNeedArrayList.addAll(helprequests.values());
        }

        @Override
        public int getCount() {
           // Log.d(TAG, String.valueOf(userInNeedArrayAdapter.size()));
            return userInNeedArrayList.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.helprequest_card, parent, false);
            }

            currposition = position;
            TextView name = convertView.findViewById(R.id.user_name);
            name.setText(userInNeedArrayList.get(position).getFirstName());

            TextView age = convertView.findViewById(R.id.user_age);
            TextView DateTime = convertView.findViewById(R.id.dandt);
            TextView decription = convertView.findViewById(R.id.txtDetails);

            Button button_shareContact =  convertView.findViewById(R.id.share_contact);
            button_shareContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sendSMS(userInNeedArrayList.get(position).)
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Triggers/"+userInNeedArrayList.get(position).getTime().toString()+"/mobile");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String userphone = dataSnapshot.getValue().toString();
                            String phoneno = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");
                            sendSMS(phoneno,"",userphone);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            age.setText(getAge(userInNeedArrayList.get(position).getDOB()));
            DateTime.setText(getTime(userInNeedArrayList.get(position).getTime()));

            String desc = userInNeedArrayList.get(position).getFirstName() + " has requested for your help!\n" +
                    "It would be ideal if you share your \n" +
                    "contact details and use location to \n" +
                    "get across before any incident occurs.";
            decription.setText(desc);
            MapView mapView = convertView.findViewById(frag_map);
            if(mapView!=null){
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(getNewOnReadyCallBack(position));
            }

            ImageView more;
            more = (ImageView) convertView.findViewById(R.id.more);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.report_menu, popupMenu.getMenu());

                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.report:
                                    openDialog(position);
                                    return true;
                            }
                            return false;
                        }

                    });
                }
            });

            return convertView;
        }
        public void sendSMS(String phoneNo, String msg,String userphone) {
            msg="Hello this is my contact number"+ phoneNo +" I am ready to help!!";
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(userphone, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
        private void openDialog(final int pos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(portal.this, R.style.MyDialogTheme);
            builder.setTitle("Issues");
            builder.setMultiChoiceItems(list, result, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if(!reports.contains(list[which])){
                        reports.add(list[which]);
                    }else{
                        reports.remove(list[which]);
                    }
                }
            });

            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String item = "";
                    for(int i = 0;i<reports.size();i++){
                        item = item + " " + reports.get(i);
                    }
                    //Log.d(TAG,item);
                    String from = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");
                    ReportUser.report(from,String.valueOf(userInNeedArrayList.get(pos).getTime()),item);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

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
                    lat = Double.parseDouble(userInNeedArrayList.get(position).getLat());
                    lon = Double.parseDouble(userInNeedArrayList.get(position).getLon());
                    LatLng location = new LatLng(lat,lon);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f));
                    googleMap.addMarker(new MarkerOptions().position(location));

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    //setMap();
                }
            };
        }

        private String getAge(String dt){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
           // Log.d(TAG,dt);
            try{
                sdf.parse(dt);
                Calendar cal = sdf.getCalendar();
                Date bdate = cal.getTime();
                Date currentdate = Calendar.getInstance().getTime();

                long diff  = currentdate.getTime() - bdate.getTime();
                long years = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)/365;



                return String.valueOf(years);

            }catch (Exception e){
                e.printStackTrace();
            }
            return "";
            //Log.d(TAG,String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)/365));
        }
        private String getTime(Long lngtime){
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(lngtime);
            System.out.println(sdf.format(resultdate));
            return sdf.format(resultdate);
        }

    }
}
