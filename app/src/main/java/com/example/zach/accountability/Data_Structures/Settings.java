package com.example.zach.accountability.Data_Structures;

/*
This class declares global settings for the app for all other classes to use
*/

import org.json.JSONArray;
import org.json.JSONObject;

public class Settings {
    //Constant Settings
    public String LocalRosterName;
    public String LocalSettingsName;
    public int MaxHistorySteps;

    //Dynamic settings
    public String CurrentRoom;
    public int RoomCount;

    public Settings(){
        //Set constant settings
        this.LocalRosterName  = "roster.json";
        this.LocalSettingsName = "settings.json";
        this.MaxHistorySteps = 20;

        //Set dynamic settings
        this.CurrentRoom = "";
        this.RoomCount = 0;
    }

    //Implement To JSON Method (also for Student)

    //Converts savable settings into json obj
    private JSONObject ToJSONObj(){
        JSONObject jObj = new JSONObject();

        try {
            jObj.put("currentRoom", this.CurrentRoom);
            jObj.put("roomCount", this.RoomCount);
        }catch (Exception e){
            e.printStackTrace();
        }

        return jObj;
    }

    //Sets dynamic settings from loaded JSON object string
    public void LoadJSON(String _string){
        try {
            JSONObject jObj = new JSONObject(_string);
            this.CurrentRoom = jObj.getString("currentRoom");
            this.RoomCount = jObj.getInt("roomCount");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String returnString;
        JSONObject jObj = this.ToJSONObj();

        returnString = jObj.toString();

        return returnString;
    }
}