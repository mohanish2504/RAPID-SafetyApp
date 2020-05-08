package com.example.safetyapp.user;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        listView = (ListView) findViewById(R.id.referal_contacts_list);


        EmergencyContact emergencyContacts[] = new EmergencyContact[2];

        MyAdapter myAdapter = new MyAdapter(this,R.layout.activity_emergency_contact,emergencyContacts);
        listView.setAdapter(myAdapter);

        TextView txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtDetails.setText("Note: Please send link to contacts and install the app in your \n" +
                "emergency contacts' device to get referral code ");
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }

    class MyAdapter extends ArrayAdapter<EmergencyContact>{

        EmergencyContact emergencyContacts[];
        Context context;

        public MyAdapter(@NonNull Context context, int resource,EmergencyContact emergencyContacts[]) {
            super(context, resource);
            this.context = context;
            this.emergencyContacts = emergencyContacts;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View emergencycontactview = layoutInflater.inflate(R.layout.activity_emergency_contact,parent,false);

            TextView textView_name = (TextView) emergencycontactview.findViewById(R.id.contacts_textview_name);
            EditText editText_code = (EditText) emergencycontactview.findViewById(R.id.contacts_textview_number);

            textView_name.setText(emergencyContacts[position].getName());
            editText_code.setHint(emergencyContacts[position].getReferal());

            return emergencycontactview;

        }
    }

    class EmergencyContact{
        String name;
        String referal;

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
