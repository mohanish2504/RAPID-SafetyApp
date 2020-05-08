package com.example.safetyapp.user;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.R;

import java.util.List;

public class ReferalActivity extends AppCompatActivity {

    ListView listView;
    String Names[] = {"Name 1","Name 2"};
    String Codes[] = {"xyz","xyz"};
    private String TAG= ReferalActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        listView = (ListView) findViewById(R.id.list);
        EmergencyContact[] emergencyContacts = new EmergencyContact[2];

        emergencyContacts[0] = new EmergencyContact();
        emergencyContacts[1] = new EmergencyContact();



        MyAdapter myAdapter = new MyAdapter(this,R.layout.activity_emergency_contact,emergencyContacts);
        listView.setAdapter(myAdapter);

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Note: Please send link to contacts and install the app in your \n" +
                "emergency contacts' device to get referral code ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }

    class MyAdapter extends ArrayAdapter<EmergencyContact>{

        EmergencyContact[] emergencyContacts;
        Context context;
        private String TAG= MyAdapter.class.getSimpleName();
        public MyAdapter(@NonNull Context context, int resource,EmergencyContact[] emergencyContacts) {
            super(context, resource);
            //Log.d(TAG,"new adapter");
            this.context = context;
            this.emergencyContacts = emergencyContacts;
           // Log.d(TAG,this.emergencyContacts[1].getName());
            //Log.d(TAG,Integer.toString(this.emergencyContacts.length));
        }

        @Override
        public int getCount() {
            return emergencyContacts.length;
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

            Log.d(TAG,this.emergencyContacts[position].getName());

            textView_name.setText(emergencyContacts[position].getName());
            editText_code.setHint(emergencyContacts[position].getReferal());

            //Log.d("Howdi","here");

            return convertView;

        }
    }

    class EmergencyContact{
        String name;
        String referal;

        public EmergencyContact(String name, String referal) {
            this.name = name;
            this.referal = referal;
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

}
