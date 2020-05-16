package com.example.safetyapp.user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import static com.example.safetyapp.user.EmergencyContact.RequestPermissionCode;

public class ReferalActivity extends AppCompatActivity {

    ListView listView;

    private String TAG= ReferalActivity.class.getSimpleName();
    private static ArrayList<EmergencyContact> emergencyContacts;
    MyAdapter myAdapter;
    Button verifyContacts,sendInvitation;
    private static Set<String> numberset = new HashSet<String>();
    TextView textView_userreferalcode;
    String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        EnableRuntimePermission();
       // ReferalGenerator.checkForReferal(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","811111111"));
        textView_userreferalcode = (TextView) findViewById(R.id.referalcode);
        //Log.d(TAG,ReferalGenerator.getReferal());
        textView_userreferalcode.setText(ReferalGenerator.getReferal());

        msg="Hi I am inviting you to be my emergency contact please download the app share referal code";

        listView = (ListView) findViewById(R.id.list);
        emergencyContacts=new ArrayList<EmergencyContact>();

        myAdapter = new MyAdapter(this,R.layout.activity_emergency_contact,emergencyContacts);
        listView.setAdapter(myAdapter);

        Button selectContact = (Button) findViewById(R.id.selectContact);
        selectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emergencyContacts.size() < 2) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 7);
                    //myAdapter.notifyDataSetChanged();
                    //Log.d(TAG,Integer.toString(emergencyContacts.size()));
                }
            }
        });

        verifyContacts = (Button) findViewById(R.id.verifyContact);
        verifyContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkForAddedContact();
            }
        });

        sendInvitation=(Button) findViewById(R.id.sendInvitation);
        sendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String n : numberset){
                    sendSMS(n,msg);
                }
            }
        });

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Note: Please send link to contacts and install the app in your \n" +
                "emergency cotacts' device to get referral code ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
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

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Referals");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //hjgjhhjb
                Map<String,String> selectedContacts = new HashMap<>();
                for(int i = 0 ; i<emergencyContacts.size();i++){
                    View view  = listView.getChildAt(i);
                    EditText code = view.findViewById(R.id.contacts_textview_number);
                    selectedContacts.put(emergencyContacts.get(i).getNumber(),code.toString());
                }

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                   String number = dataSnapshot1.getKey();
                   String code = String.valueOf(dataSnapshot1.getValue());
                   Log.d(TAG,number+" "+code);

                   if(selectedContacts.containsKey(number) && code == selectedContacts.get(number)){
                        selectedContacts.remove(number);
                   }
               }
               //Log.d(TAG,Boolean.toString(codeset.isEmpty()));
               if(selectedContacts.isEmpty()){
                   int i = 0;
                   String title = "EmergencyContact";
                   for(String n : numberset){
                       getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putString(title+String.valueOf(++i),n).apply();
                   }
                   getSharedPreferences("UserDetails",MODE_PRIVATE).edit().putInt("EC_SIZE",i).apply();
                   Toast.makeText(getApplicationContext(),"All Contacts Verified Sucessfully!",Toast.LENGTH_SHORT).show();
                   getSharedPreferences("LoginDetails",MODE_PRIVATE).edit().putBoolean("ContactsVerification",true).apply();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
               }else{
                   Toast.makeText(getApplicationContext(),"Contacts are not verified please check",Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        ArrayList<EmergencyContact> emergencyContacts;
        Context context;
        private String TAG= MyAdapter.class.getSimpleName();
        public MyAdapter(@NonNull Context context, int resource,ArrayList<EmergencyContact> emergencyContacts) {
            super(context, resource);
            this.context = context;
            this.emergencyContacts = emergencyContacts;
        }

        @Override
        public int getCount() {
            return emergencyContacts.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.activity_contacts_listview, parent, false);
            }

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

            TextView textView_name = (TextView) convertView.findViewById(R.id.contacts_textview_name);
            EditText editText_code = (EditText) convertView.findViewById(R.id.contacts_textview_number);

            //Log.d(TAG,this.emergencyContacts.get(position).getName());

            textView_name.setText(this.emergencyContacts.get(position).getName());
            editText_code.setHint(this.emergencyContacts.get(position).getReferal());

            //Log.d("Howdi","here");

            return convertView;

        }
    }

    public class EmergencyContact{
        String name;
        String referal;
        String number;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public EmergencyContact(String name, String referal,String number) {
            this.name = name;
            this.referal = referal;
            this.number = number;
        }

        public EmergencyContact() {
        }

        public String getReferal() {
            if(referal==null)referal="xyz123";
            return referal;
        }

        public void setReferal(String referal) {
            this.referal = referal;
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

        if(requestCode==7){

                EmergencyContact emergencyContact_temp;
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


                        if(number.length()>10){
                            StringBuilder num = new StringBuilder();
                            for(int i = number.length()-1;i>=number.length()-10;i--){
                                num.insert(0,number.charAt(i));
                            }
                            number = num.toString();
                        }

                        EmergencyContact emergencyContact = new EmergencyContact(name,null,number);
                        if(!numberset.contains(number)) {
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
            //}
        }
    }
}
