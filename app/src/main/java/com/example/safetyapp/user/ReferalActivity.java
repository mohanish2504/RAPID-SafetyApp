package com.example.safetyapp.user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.safetyapp.R;
import com.example.safetyapp.ReferalGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.Inflater;

import static com.example.safetyapp.user.EmergencyContact.RequestPermissionCode;

public class ReferalActivity extends AppCompatActivity {

    ListView listView;
    String Names[] = {"Name 1","Name 2"};
    String Codes[] = {"xyz","xyz"};
    private String TAG= ReferalActivity.class.getSimpleName();
    ArrayList<EmergencyContact> emergencyContacts;
    MyAdapter myAdapter;
    Button verifyContacts;
    Set<String> numberset = new HashSet<String>();
    TextView textView_userreferalcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        EnableRuntimePermission();
       // ReferalGenerator.checkForReferal(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Number","811111111"));
        textView_userreferalcode = (TextView) findViewById(R.id.referalcode);
        Log.d(TAG,ReferalGenerator.getReferal());
        textView_userreferalcode.setText(ReferalGenerator.getReferal());
        textView_userreferalcode.setTextColor(Color.parseColor("#000000"));


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


        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Note: Please send link to contacts and install the app in your \n" +
                "emergency contacts' device to get referral code ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }

    public void checkForAddedContact(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Referals");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Set<String> codeset = new HashSet<String>();
               for(int i = 0;i<numberset.size();i++){
                   View view = listView.getChildAt(i);
                   EditText editText = view.findViewById(R.id.contacts_textview_number);
                   //Log.d(TAG,editText.getText().toString());
                   codeset.add(editText.getText().toString());
            }
               //Log.d(TAG,editText.getText().toString());

               for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                   String code = dataSnapshot1.getValue().toString();
                   Log.d(TAG,code);
                   if(codeset.contains(code)){
                       codeset.remove(code);
                   }
                   if(codeset.isEmpty())break;
               }
               //Log.d(TAG,Boolean.toString(codeset.isEmpty()));
               if(codeset.isEmpty()){
                   Toast.makeText(getApplicationContext(),"All Contacts Verified Sucessfully!",Toast.LENGTH_SHORT).show();
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(getApplicationContext(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

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

    class EmergencyContact{
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
        Log.d(TAG,Integer.toString(requestCode));
        if(requestCode==7){
            //if(requestCode==RESULT_OK){
                //Log.d()
                Log.d(TAG,"here");
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
                        emergencyContact_temp = new EmergencyContact(name,"xyz",number);

                        if(!numberset.contains(number)) {
                            emergencyContacts.add(emergencyContact_temp);
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
