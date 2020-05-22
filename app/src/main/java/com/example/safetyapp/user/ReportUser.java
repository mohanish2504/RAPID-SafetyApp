package com.example.safetyapp.user;

import com.example.safetyapp.messageservice.MessageService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportUser {
    public static void report(String from,String to, String msg){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reports");
        ReportFormat reportFormat =  new ReportFormat(from,to, msg);
        Long currentTime = System.currentTimeMillis();
        databaseReference.child(String.valueOf(currentTime)).setValue(reportFormat);
    }

    public static class ReportFormat{
        String From,To,Msg;

        public ReportFormat(String from, String to, String msg) {
            From = from;
            To = to;
            Msg = msg;
        }

        public String getFrom() {
            return From;
        }

        public void setFrom(String from) {
            From = from;
        }

        public String getTo() {
            return To;
        }

        public void setTo(String to) {
            To = to;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }
    }
}
