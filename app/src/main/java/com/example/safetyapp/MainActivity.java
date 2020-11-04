package com.example.safetyapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.safetyapp.Triggers.Trigger;
import com.example.safetyapp.restarter.JobService;
import com.example.safetyapp.restarter.MyWorker;
import com.example.safetyapp.restarter.RestartServiceBroadcastReceiver;
import com.example.safetyapp.screenreceiver.ScreenOnOffReceiver;
import com.example.safetyapp.user.ReportUser;
import com.example.safetyapp.user.TutorialActivity;
import com.example.safetyapp.user.infoActivity;
import com.example.safetyapp.user.phoneno;
import com.example.safetyapp.user.portal;
import com.example.safetyapp.user.signUpActivity;
import com.example.safetyapp.user.termsAndConditionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.safetyapp.Globals.*;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ScreenOnOffReceiver screenOnOffReceiver = null;
    private static String TAG = MainActivity.class.getSimpleName();
    DatabaseReference databaseReference;
    private ActionBarDrawerToggle mToggle;
    MenuItem btnlogout;
    private static boolean activityRunning;
    private String UID;
    private static UserDetails userDetails;
    Intent sirenIntent;
    Button btnsafetystatus;
    TextView pendingrequests;
    RelativeLayout btnportal, info, mode, tutorial;
    BroadcastReceiver mynotificationreceiver,alertreceiver;
    FirebaseAuth mAuth;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    String safetystatus;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;

    Uri ringtoneUri = Uri.parse("android.resource://com.example.safetyapp/raw/siren");
    private static Ringtone siren ;
    Activity activity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"I am in MainActivity");
        activity = (Activity) this;

        if(sharedPref == null || editor == null){
            sharedPref =getSharedPreferences("Info",Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        pendingrequests = findViewById(R.id.help_request_count);
        setPendingRequests();

        mynotificationreceiver = new NotificationReceiver();
        IntentFilter intentFilter = new IntentFilter(Globals.BROADCAST);
        registerReceiver(mynotificationreceiver,intentFilter);

        alertreceiver = new AlertRecevier();
        intentFilter = new IntentFilter(Globals.BROADCAST_SAFETY);
        registerReceiver(alertreceiver,intentFilter);

        final Dialog mode_dialog =  new Dialog(this, R.style.MyDialogTheme);

        TextView mode_dialog_text = findViewById(R.id.mode_dialog);
        mode_dialog_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode_dialog.setContentView(R.layout.mode_dialog);
                mode_dialog.show();
            }
        });

        final Dialog dialog = new Dialog(this, R.style.MyDialogTheme);
        boolean dialogshown = getSharedPreferences("AcceptTerms",MODE_PRIVATE).getBoolean("status",false);
        dialog.setContentView(R.layout.terms_condition_dialog);
        dialog.setCancelable(false);
        if(!dialogshown){
            dialog.show();
        }

        TextView exit = dialog.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextView ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getSharedPreferences("AcceptTerms",MODE_PRIVATE).edit().putBoolean("status",true).apply();

            }
        });

        TextView textView = dialog.findViewById(R.id.terms_condition_link);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), termsAndConditionActivity.class);
                startActivity(i);
            }
        });



        CheckBox checkBox = dialog.findViewById(R.id.accept);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //dialog.dismiss();
                    ok.setEnabled(true);
                    getSharedPreferences("AcceptTerms",MODE_PRIVATE).edit().putBoolean("status",true).apply();
                }
            }
        });


        info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), infoActivity.class);
                startActivity(i);
            }
        });

        btnsafetystatus = (Button) findViewById(R.id.safe);


        SAFETYSTATUS = getSharedPreferences("Info",MODE_PRIVATE).getString("SafetyStatus","ON");
        setSafetyButton();

        btnsafetystatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,SAFETYSTATUS);
                ALERT();
            }
        });


        btnportal = findViewById(R.id.portal);
        btnportal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, portal.class);
                startActivity(i);
            }
        });


        tutorial = (RelativeLayout) findViewById(R.id.tutorial);
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(i);
            }
        });

        mAuth=FirebaseAuth.getInstance();

        mDrawerLayout  = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        btnlogout =(MenuItem) findViewById(R.id.logout);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mode = findViewById(R.id.mode);
        final TextView modeText = findViewById(R.id.mode_text);
        modeText.setText(MODE);
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Globals.MODE.equals("MODE | PRIVATE")){
                    modeText.setText("MODE | PUBLIC");
                    Globals.MODE = "MODE | PUBLIC";
                }else if(Globals.MODE.equals("MODE | PUBLIC")){
                    modeText.setText("MODE | PRIVATE");
                    Globals.MODE="MODE | PRIVATE";
                }
            }
        });

        UID = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","");

        setToken();
        createMethod();
        scheduleJob();
        setUserData(getSharedPreferences("UserDetails",MODE_PRIVATE).getAll());
        uploadUserData();
    }

    private void setSafetyButton(){
        if(SAFETYSTATUS.equals("OFF")) {
            btnsafetystatus.setBackgroundResource(R.drawable.unsafe_new);
            btnsafetystatus.setText("Unsafe");
            //btnsafetystatus.setBackgroundColor(Color.parseColor("#FF0000"));
        }else if(SAFETYSTATUS.equals("ON")){
            btnsafetystatus.setBackgroundResource(R.drawable.safe_new);
            btnsafetystatus.setText("Safe");
        }
    }
    private void playSiren() {
        if (siren == null) siren = RingtoneManager.getRingtone(this, ringtoneUri);
        siren.play();
    }
    private void stopSiren(){
        if(siren == null)siren = RingtoneManager.getRingtone(this, ringtoneUri);
        siren.stop();
    }
    private void STARTALERT(){
        playSiren();
        getSharedPreferences("Info",MODE_PRIVATE).edit().putString("SafetyStatus","OFF").apply();
        SAFETYSTATUS = "OFF";
        setSafetyButton();
        editor.putLong("LastTrigger",currentTriggerTime).apply();
        Trigger trigger = new Trigger();
        trigger.registerTrigger(getApplicationContext());
    }
    private void STOPALERT(){
        stopSiren();
        getSharedPreferences("Info",MODE_PRIVATE).edit().putString("SafetyStatus","ON").apply();
        SAFETYSTATUS = "ON";
        setSafetyButton();
    }
    public void ALERT(){
        if(SAFETYSTATUS.equals("OFF")) {
            STOPALERT();
        }
        else if(SAFETYSTATUS.equals("ON") && Trigger.isAcceptable(getApplicationContext()) ){
           STARTALERT();
        }else{
            Toast.makeText(getApplicationContext(),"You must wait for atleast 2 minutes to make new Trigger request",Toast.LENGTH_LONG).show();

        }
    }

    public class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Globals.BROADCAST)) setPendingRequests();
        }
    }
    public class AlertRecevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Globals.BROADCAST_SAFETY)){
                Log.d(TAG,"SAFETY ALAERT");
                SAFETYSTATUS= "ON";
                STARTALERT();
               // str = "ON";
                if(!activityRunning){
                    startActivity(getIntent());
                }

            }
        }
    }

    private void setPendingRequests(){
        int helprequests = Globals.pendingrequests;

        if(helprequests<1)pendingrequests.setVisibility(View.INVISIBLE);
        else {
            pendingrequests.setVisibility(View.VISIBLE);
            pendingrequests.setText(String.valueOf(helprequests));
            pendingrequests.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.item_count));
            Log.d(TAG,"Setting Pending Requests");
        }
    }

    void setUserData(Map userdetails_map){

        userDetails = new UserDetails(userdetails_map);
    }
    private void setToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString("Token",token).apply();
                        DatabaseReference mdatabaseReference;
                        mdatabaseReference = FirebaseDatabase.getInstance().getReference("DeviceTokens");
                        String mobile = getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number",null);
                        if(mobile!=null)mdatabaseReference.child(mobile).setValue(token);
                    }
                });
    }
    private void uploadUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("UserDetails");
        databaseReference.orderByKey().equalTo(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("Exists", String.valueOf(dataSnapshot.exists()) + UID);
                if(dataSnapshot.exists()){

                    Globals.userDetails = dataSnapshot.child(UID).getValue(UserDetails.class);
                    //Log.d("Data",userDetails.getGender());
                    getSharedPreferences("SignUpDetails",MODE_PRIVATE).edit().putBoolean("status",true).apply();
                }else
                {
                    databaseReference.child(UID).setValue(userDetails);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void createMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Project";
            String description = "new message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelID = "Notification Channel";
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void scheduleJob() {
        Log.d(TAG,"SCHEDULING JOB");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            Log.d(TAG,"i m here");
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.editprofile) {
            uploadUserData();
            Intent i = new Intent(MainActivity.this, signUpActivity.class);
            startActivity(i);
        }
        else if(id == R.id.home){
            mDrawerLayout.closeDrawers();
        }
        else if(id == R.id.terms){
            Intent i = new Intent(MainActivity.this, termsAndConditionActivity.class);
            startActivity(i);
        }
        else if(id == R.id.emergencies_contacts) {
            if (REFERAL == null) {
                String phone = getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Number", "");
                ReferalGenerator.checkForReferal(phone, MainActivity.this, false,true);
            }else{
                ReferalGenerator.LaunchActivityReferal_withNoFlags(MainActivity.this);
            }
        }
        else if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            getSharedPreferences("Info",MODE_PRIVATE).edit().putBoolean("LoginStatus",false).apply();
            getSharedPreferences("SignUpDetails",MODE_PRIVATE).edit().putBoolean("status",false).apply();
            finish();
            startActivity(new Intent(this, phoneno.class));

        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mynotificationreceiver);
        unregisterReceiver(alertreceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        setPendingRequests();
    }


    // CHECKING FOR CHINESE ROMS
    void C_ROM(){
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }
            else if ("huawei".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }
            else if ("asus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.asus.mobilemanager","com.asus.mobilemanager.autostart.AutoStartActivity"));
            }
            else {
                Log.e("other phone ", "===>");
            }
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // set Periodic Work Request
    void PW(){
       /*WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorker.class)
                        .build();*/

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 20, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueue(periodicWork);
    }


    // for moving data to Firestore don't ever make changes here
    private void moveReports_FireStore(){

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Reports");
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for(DataSnapshot values : dataSnapshot.getChildren()){
                    //Log.d(TAG,"KEY:: "+ values.getKey() + "Value:: " + values.getValue());
                    String key = values.getKey();
                    ReportUser.ReportFormat val = values.getValue(ReportUser.ReportFormat.class);

                    if(key!=null && val!=null)db.collection("Reports").document(key).set(val);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void moveUsers_FireStore(){

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("UserDetails");
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for(DataSnapshot values : dataSnapshot.getChildren()){
                    //Log.d(TAG,"KEY:: "+ values.getKey() + "Value:: " + values.getValue());
                    String key = values.getKey();
                    UserDetails val = values.getValue(UserDetails.class);

                    if(key!=null && val!=null)db.collection("Users").document(key).set(val);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void moveTokens_FireStore(){

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("DeviceTokens");
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for(DataSnapshot values : dataSnapshot.getChildren()){
                    //Log.d(TAG,"KEY:: "+ values.getKey() + "Value:: " + values.getValue());
                    String key = values.getKey();
                    String val = (String) values.getValue();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("token",val);
                    if(key!=null && val!=null)db.collection("Tokens").document(key).set(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void moveReferals_FireStore(){

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Referals");
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for(DataSnapshot values : dataSnapshot.getChildren()){
                    //Log.d(TAG,"KEY:: "+ values.getKey() + "Value:: " + values.getValue());
                    String key = values.getKey();
                    String val = (String) values.getValue();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("code",val);
                    if(key!=null && val!=null)db.collection("Referals").document(key).set(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}