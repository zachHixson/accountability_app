package com.example.zach.accountability;

/*
    Holds all global data that persists throughout the app
 */

import android.app.Application;

public class GlobalStates extends Application{
    public static StudentList StudentList;
    public static String CurrentRoom = "";
}
