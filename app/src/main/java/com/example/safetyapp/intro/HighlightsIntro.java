package com.example.safetyapp.intro;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safetyapp.R;

import java.util.ArrayList;

import static com.example.safetyapp.screenreceiver.ScreenOnOffReceiver.getContext;


public class HighlightsIntro extends AppCompatActivity {

    ListView highlights;
    ArrayList<Highlights> arrayList;

    int IDs[] = {R.drawable.medical,R.drawable.personal,R.drawable.senior};
    String TEXTs[] = {"Medical Emergencies","Physical Abuse","Senior Citizen's Help"};

    Highlightsadapter highlightsadapter;
    private static String TAG = HighlightsIntro.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_third);


        highlights = (ListView) findViewById(R.id.listview_highlights);

        highlightsadapter = new Highlightsadapter(this,R.layout.layout_highlights);
        highlights.setAdapter(highlightsadapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),AlertAndPermissions.class);
                startActivity(intent);
            }
        }, IDs.length*2000);
    }

    public class Highlightsadapter extends ArrayAdapter<Highlights> {
        ArrayList<Highlights> highlights;
        public Highlightsadapter(@NonNull Context context, int resource) {
            super(context, resource);
            highlights = new ArrayList<>();
            for(int i = 0;i<3;i++){
                HighlightsIntro.Highlights highlight = new HighlightsIntro.Highlights(IDs[i],TEXTs[i]);
                highlights.add(highlight);
            }
        }

        @Override
        public int getCount() {
            return highlights.size();
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.layout_highlights, parent, false);
            }


            ImageView imageView = convertView.findViewById(R.id.highlights_imgview);
            TextView textView = convertView.findViewById(R.id.highlights_textview);
            Resources res = getResources();

            imageView.setImageDrawable(res.getDrawable(highlights.get(position).getImg()));
            textView.setText(highlights.get(position).getText());


            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
            animation.setStartOffset((position+1)*1500);
            animation.setDuration(1000);
            convertView.startAnimation(animation);

            Log.d(TAG,"doneANimation");

            return convertView;
        }
    }


    private class Highlights{
        int img;
        String text;

        public Highlights(int img, String text) {
            this.img = img;
            this.text = text;
        }

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
