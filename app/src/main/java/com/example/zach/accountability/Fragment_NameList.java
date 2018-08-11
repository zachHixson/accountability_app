package com.example.zach.accountability;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageButton;

import java.util.ArrayList;

public class Fragment_NameList extends Fragment{
    boolean isPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_namelist, container, false);

        //Create onclick listener for add students button
        ImageButton btnAdd = (ImageButton)returnView.findViewById(R.id.addButton);

        View.OnClickListener btnAddOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Open new activity
                if (!isPaused) {
                    setPaused(true);
                    ((ActivityMain) getActivity()).openAddMenu();
                }
            }
        };

        btnAdd.setOnClickListener(btnAddOcl);

        return returnView;
    }

    public boolean updateList(StudentList _studentList, String _currentRoom){
        //Define Variables
        Student student;
        String currentRoom = _currentRoom;
        Bundle argBundle;

        //Clear list from before
        clearList();

        //Begin Fragment transaction
        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();

        //Replace contents of the container with list of student fragments
        for (int i = 0; i < _studentList.Size(); i++){
            //Set current student info
            student = _studentList.GetStudent(i);


            //Only create fragment for display if student is in the room
            if(student.GetRoom().equals(currentRoom)) {
                //create new instance of fragment
                Fragment_Student studentFrag = new Fragment_Student();

                //Set arguments to pass down to fragment
                argBundle = new Bundle();
                argBundle.putInt("id", student.GetId());
                studentFrag.setArguments(argBundle);

                //add the fragment to the listView
                fragTrans.add(R.id.nameList, studentFrag, Integer.toString(student.GetId()));
                fragTrans.addToBackStack(null);
            }
        }

        //Commit changes to fragment
        fragTrans.commit();


        return true;
    }

    //Clear all names from list
    public boolean clearList(){
        try {
            LinearLayout nameHolder = (LinearLayout) getView().findViewById(R.id.nameList);

            if (((LinearLayout) nameHolder).getChildCount() > 0) {
                ((LinearLayout) nameHolder).removeAllViews();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    public boolean removeName(Integer _id){
        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();

        //Check to see if fragment exists
        Fragment_Student f = (Fragment_Student) getFragmentManager().findFragmentByTag(Integer.toString(_id));

        if (f != null) {
            f.CleanUp();
            fragTrans.remove(f);
            fragTrans.commit();
        }

        //clean up
        fragTrans = null;
        f = null;

        return true;
    }

    public void setPaused(boolean _inp){
        //set the paused state based on the input
        isPaused = _inp;
    }
}
