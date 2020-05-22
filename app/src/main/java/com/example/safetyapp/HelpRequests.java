package com.example.safetyapp;

import java.util.HashMap;
import java.util.Map;

public class HelpRequests {
    private static Map< Long ,UserInNeed> usersinneed = new HashMap< Long , UserInNeed>();

    public static boolean addUser(Long time,UserInNeed userinneed){

       if(!usersinneed.containsKey(time)){
           usersinneed.put(time,userinneed);
           return true;
       }
       return false;
       //Log.d("Size", String.valueOf(usersinneed.size()));
    }

    public static Map<Long, UserInNeed> getUsers(){return usersinneed;}

    public static int currentRequests(){
        return usersinneed.size();
    }

    public static class UserInNeed extends UserDetails{
        String lat,lon;
        Long time;


        public UserInNeed(Map<String,String> map){
            super(map.get("firstName"),map.get("lastName"),map.get("DOB"),map.get("gender"),map.get("city"));
            this.lat = map.get("lat");
            this.lon = map.get("lon");
            this.time = Long.valueOf(map.get("time"));
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

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }
    }


}
