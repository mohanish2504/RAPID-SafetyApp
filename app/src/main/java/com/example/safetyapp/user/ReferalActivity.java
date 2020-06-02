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

import static com.example.safetyapp.user.EmergencyContact.RequestPermissionCode;

public class ReferalActivity extends AppCompatActivity {

    ListView listView;

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
       // ReferalGenerator.checkForReferal(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","811111111"));
        textView_userreferalcode = (TextView) findViewById(R.id.referalcode);
        //Log.d(TAG,ReferalGenerator.getReferal());
        textView_userreferalcode.setText(Globals.REFERAL);

        msg="Hi I am inviting you to be my emergency contact please download the app share referal code";

        listView = (ListView) findViewById(R.id.list);


        myAdapter = new MyAdapter(this,R.layout.activity_emergency_contact);
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
                if(numberset.size()==0){
                    Toast.makeText(getApplicationContext(),"Please add atleast one contact",Toast.LENGTH_LONG).show();
                    return;
                }
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
                int numberstoVerify = emergencyContacts.size();
                for(int i = 0 ; i<emergencyContacts.size();i++){
                    View view  = listView.getChildAt(i);
                    String number = emergencyContacts.get(i).getNumber();
                    EditText editTextcode = (EditText) view.findViewById(R.id.contacts_textview_number);

                    if(dataSnapshot.hasChild(number)){
                        String databasecode = (String) dataSnapshot.child(number).getValue();
                        String code = editTextcode.getText().toString();
                        if(!databasecode.equals(code)) {
                            Toast.makeText(getApplicationContext(),"One of the entered code is wrong",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        emergencyContacts.get(i).setReferal(databasecode);
                        numberstoVerify--;
                    }
                }

               if(numberstoVerify>0){
                   Toast.makeText(getApplicationContext(),"Contacts are not verified please check",Toast.LENGTH_SHORT).show();
                   return;
               }

                Globals.emergencyContactslist.addAll(emergencyContacts);

                getSharedPreferences("LoginDetails",MODE_PRIVATE).edit().putBoolean("ContactsVerification",true).apply();
                Toast.makeText(getApplicationContext(),"All Contacts Verified Sucessfully!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
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

        //ArrayList<EmergencyContact> emergencyContacts;
        Context context;
        private String TAG= MyAdapter.class.getSimpleName();
        public MyAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            this.context = context;
            //this.emergencyContacts = emergencyContacts;
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
            EditText editText_code = (EditText) convertView.findViewById(R.id.contacts_textview_number);
            Button button_remove = (Button) convertView.findViewById(R.id.contacts_button_remove);
            //Log.d(TAG,this.emergencyContacts.get(position).getName());

            textView_name.setText(emergencyContacts.get(position).getName());
            String referal = emergencyContacts.get(position).getReferal();
            if(referal==null) editText_code.setHint("referal");
            else editText_code.setText(emergencyContacts.get(position).getReferal());

            button_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberset.remove(emergencyContacts.get(position).getNumber());
                    emergencyContacts.remove(position);
                    notifyDataSetChanged();
                }
            });
            //Log.d("Howdi","here");

            return convertView;

        }
    }

    public class EmergencyContact{
        String name;
        String referal;
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
            this.referal = referal;
            this.number = number;
        }

        public EmergencyContact() {
        }

        public String getReferal() {
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
                //}

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Error Please Try Again!",Toast.LENGTH_SHORT).show();

            }
        }
    }
}
