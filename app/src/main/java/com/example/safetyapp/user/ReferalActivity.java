package com.example.safetyapp.user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.safetyapp.BuildConfig;
import com.example.safetyapp.Globals;
import com.example.safetyapp.MainActivity;
import com.example.safetyapp.R;
import com.example.safetyapp.ReferalGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.Phonenumber;



public class ReferalActivity extends AppCompatActivity {

    ListView listView;
    int RequestPermissionCode = 1212;
    private String TAG= ReferalActivity.class.getSimpleName();
    private static ArrayList<EmergencyContact> emergencyContacts=new ArrayList<EmergencyContact>();;
    private MyAdapter myAdapter;
    Button verifyContacts,sendInvitation;
    private static Set<String> numberset = new HashSet<String>();
    TextView textView_userreferalcode;
    String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        EnableRuntimePermission();
        /*textView_userreferalcode = (TextView) findViewById(R.id.referalcode);
        textView_userreferalcode.setText(Globals.REFERAL);*/

        msg="Hi I am inviting you to be my emergency contact please download the Rapid app share referal code Link: https://drive.google.com/file/d/1Sd1ic4HetX-DXh-k-VT3c3knc_w59H1g/view?usp=sharing";

        listView = (ListView) findViewById(R.id.list);


        myAdapter = new MyAdapter(this,R.layout.activity_contacts_listview);
        listView.setAdapter(myAdapter);

        Button selectContact = (Button) findViewById(R.id.selectContact);
        selectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emergencyContacts.size() < 2) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 7);
                }
            }
        });

        verifyContacts = (Button) findViewById(R.id.Done);
        verifyContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(numberset.size()>2){
                    Toast.makeText(getApplicationContext(),"Sorry cant add more than 2 numbers",Toast.LENGTH_LONG).show();;
                    return;
                }
                checkForAddedContact();
            }
        });

        sendInvitation=(Button) findViewById(R.id.sendInvitation);
        sendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = msg +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }

                /*if(isSMSPermissionGranted()){
                    for(String n : numberset){
                        sendSMS(n,msg);
                    }
                }*/


            }
        });

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Note: Please send link to contacts and install the app in your \n" +
                "emergency cotacts' device to get referral code ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }


    public  boolean isSMSPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent to "+ phoneNo,
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void checkForAddedContact(){

        if(numberset.size()<1){
            Toast.makeText(getApplicationContext(),"Please select atleast one emergency contact!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(numberset.size()>2){
            emergencyContacts.clear();
            emergencyContacts.addAll(Globals.emergencyContactslist);
            Toast.makeText(getApplicationContext(),"Cant add more than 2 numbers",Toast.LENGTH_SHORT).show();
            return;
        }

        Globals.emergencyContactslist.addAll(emergencyContacts);
        Toast.makeText(getApplicationContext(),"All Contacts Saved Sucessfully!",Toast.LENGTH_SHORT).show();

        boolean loginstatus = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getBoolean("Status",false);
        boolean contactsVerified = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE).getBoolean("ContactsVerification",false);

        if(contactsVerified && loginstatus)return;

        getSharedPreferences("LoginDetails",MODE_PRIVATE).edit().putBoolean("ContactsVerification",true).apply();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();



    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            Toast.makeText(getApplicationContext(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS}, RequestPermissionCode);
        }
    }

    class MyAdapter extends ArrayAdapter<EmergencyContact>{

        Context context;
        private String TAG= MyAdapter.class.getSimpleName();
        public MyAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            this.context = context;
        }

        @Override
        public int getCount() {
            return emergencyContacts.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.activity_contacts_listview, parent, false);
            }

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

            TextView textView_name = (TextView) convertView.findViewById(R.id.contacts_textview_name);
            //EditText editText_code = (EditText) convertView.findViewById(R.id.contacts_textview_number);
            Button button_remove = (Button) convertView.findViewById(R.id.contacts_button_remove);

            textView_name.setText(emergencyContacts.get(position).getName());

           // if(referal==null) editText_code.setHint("referal");
          //  else editText_code.setText(emergencyContacts.get(position).getReferal());

            button_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberset.remove(emergencyContacts.get(position).getNumber());
                    Globals.emergencyContactslist.clear();
                    emergencyContacts.remove(position);
                    Globals.emergencyContactslist.addAll(emergencyContacts);
                    notifyDataSetChanged();
                }
            });

            return convertView;

        }
    }

    public class EmergencyContact{
        String name;
        String number;
        int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public EmergencyContact(String name, String referal,String number) {
            this.name = name;
            this.number = number;
        }

        public EmergencyContact() {
        }





        public String getName() {
            if(name==null)name = "Name";
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==7) {

            EmergencyContact emergencyContact_temp;
            try {

                Uri contactData = data.getData();
                String number = "";
                String name = "";
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (hasPhone.equals("1")) {
                    Cursor phones = getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                        name = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replaceAll("[-() ]", "");

                        Log.d(TAG, number);
                        if (number.length() > 10) {
                            StringBuilder num = new StringBuilder();
                            for (int i = number.length() - 1; i >= number.length() - 10; i--) {
                                num.insert(0, number.charAt(i));
                            }
                            number = num.toString();
                        }

                        if (number.equals(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Number", ""))) {
                            Toast.makeText(getApplicationContext(), "Your number cannot be used as emergency contact", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        EmergencyContact emergencyContact = new EmergencyContact(name+"("+number+")", null, number);
                        if (!numberset.contains(number)) {
                            emergencyContacts.add(emergencyContact);
                            myAdapter.notifyDataSetChanged();
                            numberset.add(number);
                        }
                    }
                    phones.close();
                } else {
                    Toast.makeText(getApplicationContext(), "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Error Please Try Again!",Toast.LENGTH_SHORT).show();

            }
        }
    }
}
