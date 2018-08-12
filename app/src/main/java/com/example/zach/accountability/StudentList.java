package com.example.zach.accountability;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentList {
    private ArrayList<Student> StudentArray = new ArrayList<>();;
    private int RoomCount;

    public StudentList(){
        this.RoomCount = 0;
    }

    public void PopulateFromJSONString(String _jsonString){
        try{
            JSONArray mainArray;
            JSONObject studentObj;

            mainArray = new JSONArray(_jsonString);

            for (int i = 0; i < mainArray.length(); i++){
                studentObj = mainArray.getJSONObject(i);

                this.StudentArray.add(new Student(studentObj, i));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void AddTemp(String _fName, String _lName, String _currentRoom){
        this.StudentArray.add(new Student(_fName, _lName, _currentRoom, this.StudentArray.size()));
    }

    public String ToJSONString(){
        JSONArray jsonArray = new JSONArray();
        Student currentStudent;
        String returnString = "";

        for (int i = 0; i < this.StudentArray.size(); i++){
            currentStudent = this.StudentArray.get(i);

            if (!currentStudent.IsMarkedForDeletion()){
                try{
                    JSONObject jsonObject = currentStudent.ToJSONObj();
                    jsonArray.put(jsonObject);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        returnString = jsonArray.toString();
        return returnString;
    }

    public Student GetStudent(int _id){
        return this.StudentArray.get(_id);
    }

    public void SetStudentRoom(int _id, String _room){
        this.StudentArray.get(_id).SetRoom(_room);
    }

    //Clears list of all students in the current room
    public void ClearList(){
        for (int i = 0; i < this.StudentArray.size(); i++){
            this.StudentArray.get(i).SetRoom("");
        }
    }

    public void DeleteStoredList(){
        StudentArray.clear();
    }

    public int Size(){
        return this.StudentArray.size();
    }
}
