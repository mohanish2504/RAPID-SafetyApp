/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.safetyapp;

import com.example.safetyapp.user.ReferalActivity;

import java.util.ArrayList;

public class Globals {
    public static ArrayList<ReferalActivity.EmergencyContact> emergencyContactslist = new ArrayList<>();
    public static final String RESTART_INTENT = "com.example.project.restarter";
    public static final String RESTART_SCREEN_ONOFF_INTENT = "com.example.project.restarter";
    public static int pendingrequests;
    public static UserDetails userDetails = new UserDetails();
    public static String[] tutorialIDs = {"T7aNSRoDCmg","Gx3_x6RH1J4","M4_8PoRQP8w","-V4vEyhWDZ0","KVpxP3ZZtAc","9m-x64bKfR4"};
    //
}
