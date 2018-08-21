package com.example.zach.accountability;

import org.json.JSONObject;

/*
A Class that defines an individual student
*/

public class Student {
    private String FirstName;
    private String LastName;
    private String Info;
    private String Days;
    private String Apm;
    private String Room;
    private int Id;
    private boolean IsTemporary;
    private boolean MarkedForDeletion;
    private boolean IsSelected;

    public Student(JSONObject _jObj, int _id){
        try {
            this.FirstName = _jObj.getString("fName");
            this.LastName = _jObj.getString("lName");
            this.Info = _jObj.getString("allergies");
            this.Days = _jObj.getString("days");
            this.Apm = _jObj.getString("apm");
            this.Room = _jObj.getString("room");
            this.Id = _id;
            this.IsTemporary = _jObj.getBoolean("isTemp");
            this.MarkedForDeletion = false;
            this.IsSelected = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Student(String _firstName, String _lastName, String _allergies, String _days, String _apm, String _room, int _id){
        this.FirstName = _firstName;
        this.LastName = _lastName;
        this.Info = _allergies;
        this.Days = _days;
        this.Apm = _apm;
        this.Room = _room;
        this.Id = _id;
        this.IsTemporary = false;
        this.MarkedForDeletion = false;
        this.IsSelected = false;
    }

    public Student(String _firstName, String _lastName, String _room, int _id){
        this.FirstName = _firstName;
        this.LastName = _lastName;
        this.Info = "";
        this.Days = "iiiii";
        this.Apm = "b";
        this.Room = _room;
        this.Id = _id;
        this.IsTemporary = true;
        this.MarkedForDeletion = false;
        this.IsSelected = false;
    }

    public Student() {
        this.FirstName = "Joe";
        this.LastName = "Smoegan";
        this.Info = "<This is a placeholder name, and should not be here, please contact App Developer>";
        this.Room = "";
        this.Days = "iiiii";
        this.Apm = "b";
        this.Id = -1;
        this.IsTemporary = true;
        this.MarkedForDeletion = true;
        this.IsSelected = false;
    }

    public String GetFirstName(){
        return this.FirstName;
    }

    public String GetLastName(){
        return this.LastName;
    }

    public String GetInfo(){
        return this.Info;
    }

    public String GetDays(){
        return this.Days;
    }


    public String GetApm(){
        return this.Apm;
    }

    public void SetRoom(String _room){
        this.Room = _room;
    }

    public String GetRoom(){
        return this.Room;
    }

    public int GetId(){
        return this.Id;
    }

    public void SetId(int _id){
        this.Id = _id;
    }

    public boolean IsAdded(){
        if (!this.GetRoom().equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean IsTemporary(){
        return this.IsTemporary;
    }

    public boolean IsMarkedForDeletion(){
        return this.MarkedForDeletion;
    }

    public void MarkForDeletion(){
        if (this.IsTemporary) {
            this.MarkedForDeletion = true;
        }
    }

    public boolean IsScheduled(String _curDay){
        for (int i = 0; i < _curDay.length(); i++){
            if (_curDay.charAt(i) == this.Days.charAt(i) ){
                return true;
            }
        }

        return false;
    }

    public boolean IsSelected(){
        return this.IsSelected;
    }

    public void SetSelected(boolean status){
        this.IsSelected = status;
    }

    public JSONObject ToJSONObj(){
        JSONObject jObj = new JSONObject();

        try{
            jObj.put("fName", this.FirstName);
            jObj.put("lName", this.LastName);
            jObj.put("allergies", this.Info);
            jObj.put("days", this.Days);
            jObj.put("apm", this.Apm);
            jObj.put("room", this.Room);
            jObj.put("isTemp", this.IsTemporary);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  jObj;
    }
}
