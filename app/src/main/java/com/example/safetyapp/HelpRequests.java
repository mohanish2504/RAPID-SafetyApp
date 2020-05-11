package com.example.safetyapp;

import android.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class HelpRequests {
    private static ArrayList<UserInNeed> usersinneed = new ArrayList<>() ;

    public static void addUser(UserInNeed userinneed){
       usersinneed.add(userinneed);
       Log.d("Size", String.valueOf(usersinneed.size()));
    }

    public static ArrayList<UserInNeed> getUsers(){return usersinneed;}

    public static int currentRequests(){
        return usersinneed.size();
    }

    public static class UserInNeed extends UserDetails{
        String lat,lon;


        public UserInNeed(Map<String,String> map){
            super(map.get("firstName"),map.get("lastName"),map.get("DOB"),map.get("gender"),map.get("city"));
            this.lat = map.get("lat");
            this.lon = map.get("lon");
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }
    }


}
