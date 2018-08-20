package com.example.zach.accountability;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import android.util.Log;

public class Fragment_NameList extends Fragment{
    private RecyclerView                 studentRecycView;
    private RecyclerView.LayoutManager   recycLayM;
    private NameList_RecyclerViewAdapter recycAdpt;
    boolean isPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_namelist, container, false);

        studentRecycView = (RecyclerView) returnView.findViewById(R.id.mainList_RecyView);
        recycLayM = new LinearLayoutManager(this.getActivity());
        recycAdpt = new NameList_Rem_RecyclerViewAdapter(GlobalStates.StudentList, getContext());

        studentRecycView.setLayoutManager(recycLayM);
        studentRecycView.setAdapter(recycAdpt);

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

    public boolean updateList(StudentList _studentList){
        recycAdpt.updateFilter(_studentList);
        return true;
    }

    public void setPaused(boolean _inp){
        //set the paused state based on the input
        isPaused = _inp;
    }
}
