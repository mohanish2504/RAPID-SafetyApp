package com.example.safetyapp.intro;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safetyapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    public ThirdFragment() {
        // Required empty public constructor
    }

    ListView highlights;
    ArrayList<Highlights> arrayList;

    int IDs[] = {R.drawable.medical,R.drawable.personal,R.drawable.senior};
    String TEXTs[] = {"Medical \n Emergencies","Physical Abuse","Senior Citizen's Help"};

    private Highlightsadapter highlightsadapter;
    private static String TAG = ThirdFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.thirdfragement, container, false);
        highlights = (ListView) currentView.findViewById(R.id.listview_highlights);

        highlightsadapter = new Highlightsadapter(getContext(),R.layout.layout_highlights);
        highlights.setAdapter(highlightsadapter);

        /*for(int i = 0;i<3;i++){
            Highlights highlight = new Highlights(IDs[i],TEXTs[i]);
            highlightsadapter.add(highlight);
            highlightsadapter.notifyDataSetChanged();
        }*/

       // LayoutAnimationController layoutAnimationController = new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(),R.anim.left_in),2000);
       // highlights.startAnimation(layoutAnimationController.getAnimation());

        return currentView;
    }

    public class Highlightsadapter extends ArrayAdapter<Highlights>{
        ArrayList<Highlights> highlights;
        public Highlightsadapter(@NonNull Context context, int resource) {
            super(context, resource);
            highlights = new ArrayList<>();
            for(int i = 0;i<3;i++){
                Highlights highlight = new Highlights(IDs[i],TEXTs[i]);
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
            animation.setDuration(1500);
            convertView.startAnimation(animation);

            Log.d(TAG,"doneANimation");

            return convertView;
        }
    }


    class Highlights{
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
