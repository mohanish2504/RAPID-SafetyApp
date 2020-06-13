/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.safetyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.example.safetyapp.user.ReferalActivity;

import java.util.ArrayList;

public class Globals {
    public static ArrayList<ReferalActivity.EmergencyContact> emergencyContactslist = new ArrayList<>();
    public static final String RESTART_INTENT = "com.example.project.restarter";
    public static final String RESTART_SCREEN_ONOFF_INTENT = "com.example.project.restarter";
    public static String REFERAL;
    public static int pendingrequests;
    public static final String BROADCAST = "com.example.safetyapp.messageservice.android.action.broadcast";
    public static final String BROADCAST_SAFETY = "com.example.safetyapp.screenreceiver.android.action.broadcast";
    public static UserDetails userDetails = new UserDetails();
    public static String MODE = "MODE | PUBLIC";
    public static String[] tutorialIDs = {"T7aNSRoDCmg","Gx3_x6RH1J4","M4_8PoRQP8w","-V4vEyhWDZ0","KVpxP3ZZtAc","9m-x64bKfR4"};
    public static String[] tutorialTITLEs = {"7 Self-Defense Techniques for Women from Professionals","27 SELF-DEFENSE TRICKS FOR WOMEN","Simple Self Defense Moves You Should Know","5 Choke Hold Defenses Women MUST Know | Self Defense | Aja Dang","5 Self-Defense Moves Every Woman Should Know | HER Network","Self Defence Techniques for girls by Delhi Police Part 1"};
    public static long minimumTriggerTime = 2*60*1000;
    public static long currentTriggerTime , previousTriggerTime ;
    public static String SAFETYSTATUS = "OFF";
    public static String USERNUMBER= null;
    public static Context servicecontext;
    private static int REQUEST_CODE = 1;


    private static String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public static void getPermissions(Context context, Activity activity){
        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_CODE);
        }

    }
    //
}
