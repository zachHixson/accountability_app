package com.example.zach.accountability.Data_Structures;

/*
This class declares global settings for the app for all other classes to use
*/

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.zach.accountability.IO.FileIO;

public class Settings {
    //Constant Settings
    public String LocalRosterName;
    public String LocalSettingsName;
    public int MaxHistorySteps;

    //Dynamic settings
    public String CurrentRoom;
    public int RoomCount;
    public boolean sortByFirst;

    public Settings(){
        //Set constant settings
        this.LocalRosterName  = "roster.json";
        this.LocalSettingsName = "settings.json";
        this.MaxHistorySteps = 20;

        //Set dynamic settings
        this.CurrentRoom = "";
        this.RoomCount = 0;
        this.sortByFirst = true;
    }

    //Converts savable settings into json obj
    private JSONObject ToJSONObj(){
        JSONObject jObj = new JSONObject();

        try {
            jObj.put("currentRoom", this.CurrentRoom);
            jObj.put("roomCount", this.RoomCount);
            jObj.put("sortByFirst", this.sortByFirst);
        }catch (Exception e){
            e.printStackTrace();
        }

        return jObj;
    }

    public void Load(Context ctx){
        FileIO fileIO = new FileIO(ctx);
        try{
            String rawSettings = fileIO.OpenLocalFile(this.LocalSettingsName);
            this.LoadJSON(rawSettings);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void Save(Context ctx){
        FileIO fileIO = new FileIO(ctx);
        fileIO.SaveLocalFile(this.LocalSettingsName, this.toString());
    }

    //Sets dynamic settings from loaded JSON object string
    private void LoadJSON(String _string){
        try {
            JSONObject jObj = new JSONObject(_string);
            this.CurrentRoom = jObj.getString("currentRoom");
            this.RoomCount = jObj.getInt("roomCount");
            this.sortByFirst = jObj.getBoolean("sortByFirst");
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