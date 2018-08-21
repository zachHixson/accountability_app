package com.example.zach.accountability;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.*;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class Fragment_AddStudent extends Fragment{
    //Declare variables for the scope of the fragment
    Student thisStudent;
    View mainRootView;
    boolean selected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Pull in arguments
        thisStudent = GlobalStates.StudentList.GetStudent(getArguments().getInt("id"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_student, container, false);
        mainRootView = rootView;

        //Set the fragment data to the student data
        TextView txtFirstName = (TextView)rootView.findViewById(R.id.firstName);
        txtFirstName.setText(thisStudent.GetFirstName());

        TextView txtLastName = (TextView)rootView.findViewById(R.id.lastName);
        txtLastName.setText(thisStudent.GetLastName());

        //Remove the "Info" button if there is no extra info
        if (thisStudent.GetInfo().equals("")){
            ImageButton infoBtn = (ImageButton)rootView.findViewById(R.id.addInfoButton);
            infoBtn.setVisibility(ImageButton.GONE);
        }

        //if the student is not temporary, remove temporary button
        if(!thisStudent.IsTemporary()){
            ImageButton btnDeleteTemp = (ImageButton)rootView.findViewById(R.id.addDeleteTempButton);
            btnDeleteTemp.setVisibility(ImageButton.GONE);
        }

        //Set onclick listener for Add button
        ImageButton btnAdd = (ImageButton)rootView.findViewById(R.id.addButton);

        View.OnClickListener btnAddOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Decide if student is already selected or not
                if (selected){ //If the student is already selected, deselect them
                    deselectSelf();
                }
                else{//if the student isn't selected, then select them
                    selectSelf();
                }
            }
        };

        btnAdd.setOnClickListener(btnAddOcl);

        //set onclick listener for Info Button
        ImageButton btnInfo = (ImageButton)rootView.findViewById(R.id.addInfoButton);

        View.OnClickListener btnShowInfo = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String infoBoxContent = String.format("%1$s", thisStudent.GetInfo());
                //((ActivityAddStudent)getActivity()).openInfoBox(infoBoxContent);
            }
        };

        btnInfo.setOnClickListener(btnShowInfo);

        //Set onclick listener for Delete Temporary button
        ImageButton btnDeleteTemp = (ImageButton)rootView.findViewById(R.id.addDeleteTempButton);

        View.OnClickListener btnDeleteTempOcl = new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                deselectSelf();
                ((ActivityAddStudent)getActivity()).deleteTemp(thisStudent.GetId());
            }
        };

        btnDeleteTemp.setOnClickListener(btnDeleteTempOcl);

        return rootView;
    }

    public void deselectSelf(){
        selected = false;
        //((ActivityAddStudent)getActivity()).deselectStudent(thisStudent.GetId());

        //Change background color
        LinearLayout thisLayout = (LinearLayout)mainRootView.findViewById(R.id.itemLayout);
        thisLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public void selectSelf(){
        selected = true;
        //((ActivityAddStudent)getActivity()).selectStudent(thisStudent.GetId());

        //Change background color
        LinearLayout thisLayout = (LinearLayout)mainRootView.findViewById(R.id.itemLayout);
        thisLayout.setBackgroundColor(Color.parseColor("#62D1FF"));
    }
}
