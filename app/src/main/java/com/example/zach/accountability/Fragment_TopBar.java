package com.example.zach.accountability;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class Fragment_TopBar extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topbar, container, false);

        return rootView;
    }

    public boolean updateRoomCount(int _count){
        //Update the count TextView to display new number
        TextView countText = (TextView)getView().findViewById(R.id.roomCount);
        countText.setText(String.format("%1$s: %2$s", getString(R.string.Count), Integer.toString(_count)));

        return true;
    }
}