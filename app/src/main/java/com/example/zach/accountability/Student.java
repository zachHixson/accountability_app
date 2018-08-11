package com.example.zach.accountability;

import org.json.JSONObject;

/*
A Class that defines an individual student
*/

public class Student {
    private String FirstName;
    private String LastName;
    private String Info;
    private String Room;
    private int Id;
    private boolean IsTemporary;
    private boolean MarkedForDeletion;

    public Student(String[] _props, int _id){
        this.FirstName = _props[0];
        this.LastName = _props[1];
        this.Info = _props[2];
        this.Room = _props[3];
        this.Id = _id;
        this.IsTemporary = java.lang.Boolean.parseBoolean(_props[4]);
        this.MarkedForDeletion = false;
    }

    public Student(String _firstName, String _lastName, String _allergies, String _room, int _id){
        this.FirstName = _firstName;
        this.LastName = _lastName;
        this.Info = _allergies;
        this.Room = _room;
        this.Id = _id;
        this.IsTemporary = false;
        this.MarkedForDeletion = false;
    }

    public Student(String _firstName, String _lastName, String _room, int _id){
        this.FirstName = _firstName;
        this.LastName = _lastName;
        this.Info = "";
        this.Room = _room;
        this.Id = _id;
        this.IsTemporary = true;
        this.MarkedForDeletion = false;
    }

    public Student(JSONObject _jObj, int _id){
        try {
            this.FirstName = _jObj.getString("fName");
            this.LastName = _jObj.getString("lName");
            this.Info = _jObj.getString("allergies");
            this.Room = _jObj.getString("room");
            this.Id = _id;
            this.IsTemporary = _jObj.getBoolean("isTemp");
            this.MarkedForDeletion = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Student() {
        this.FirstName = "Joe";
        this.LastName = "Smoegan";
        this.Info = "<This is a placeholder name, and should not be here, please contact App Developer>";
        this.Room = "";
        this.Id = -1;
        this.IsTemporary = true;
        this.MarkedForDeletion = true;
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

    public JSONObject ToJSONObj(){
        JSONObject jObj = new JSONObject();

        try{
            jObj.put("fName", this.FirstName);
            jObj.put("lName", this.LastName);
            jObj.put("allergies", this.Info);
            jObj.put("room", this.Room);
            jObj.put("isTemp", this.IsTemporary);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  jObj;
    }
}
