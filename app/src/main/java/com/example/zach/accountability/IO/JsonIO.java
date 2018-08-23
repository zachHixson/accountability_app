package com.example.zach.accountability.IO;

import com.example.zach.accountability.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonIO {
    public static ArrayList<Student> JSONToStudentList(String _string){
        //Declare variables for function scope
        ArrayList<Student> returnArray = new ArrayList<Student>();

        //Parse json into studentList
        try{
            //Declare variables for local scope
            JSONArray mainArray;
            JSONObject studentObj;

            //Store raw JSON data in JSON Array format
            mainArray = new JSONArray(_string);

            //Iterate through JSON Array, and store info in studentList
            for (int i = 0; i < mainArray.length(); i++){
                studentObj = mainArray.getJSONObject(i); //Get Individual student JSON object from array

                //Add student to list
                returnArray.add(new Student(studentObj, i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return returnArray;
    }

    public static String[] JsonStringToArray(String _string){
        String returnArray[] = {"thing"};

        return returnArray;
    }

    public static String StudentListToString(ArrayList<Student> _studentList){
        String contents;

        //Serializes JSON
        JSONArray jArray = new JSONArray();

        for (int i = 0; i < _studentList.size(); i++){
            //if the student is not marked for deletion, save them
            if (!_studentList.get(i).IsMarkedForDeletion()) {
                try {
                    JSONObject jObj = _studentList.get(i).ToJSONObj();
                    jArray.put(jObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        contents = jArray.toString();

        return contents;
    }
}
