package com.example.zach.accountability;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

public class Fragment_Student extends Fragment{
    //Declare variables for the scope of the fragment
    Student thisStudent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //pull in arguments
        thisStudent = GlobalStates.StudentList.GetStudent(getArguments().getInt("id"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_student, container, false);
        final Context context = getActivity();

        //Set the fragment data to the student data
        TextView txtFirstName = (TextView)rootView.findViewById(R.id.firstName);
        txtFirstName.setText(thisStudent.GetFirstName());

        TextView txtLastName = (TextView)rootView.findViewById(R.id.lastName);
        txtLastName.setText(thisStudent.GetLastName());

        //Remove the "Info" button if there is no extra info
        if (thisStudent.GetInfo().equals("")){
            ImageButton infoBtn = (ImageButton)rootView.findViewById(R.id.infoButton);
            infoBtn.setVisibility(ImageButton.GONE);
        }

        //Remove the "Delete Temporary" button if the student is not temporary
        if(!thisStudent.IsTemporary()){
            ImageButton infoBtn = (ImageButton)rootView.findViewById(R.id.deleteTempButton);
            infoBtn.setVisibility(ImageButton.GONE);
        }

        //Create Remove button listener
        ImageButton btnRemove = (ImageButton)rootView.findViewById(R.id.removeButton);

        View.OnClickListener btnRemoveOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Calls removeName method in the main activity
                ((ActivityMain)getActivity()).removeName(thisStudent.GetId(), false);
            }
        };

        btnRemove.setOnClickListener(btnRemoveOcl);

        //Create delete temporary button listener
        ImageButton btnDeleteTemp = (ImageButton)rootView.findViewById(R.id.deleteTempButton);

        View.OnClickListener btnDeleteTempOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Add are you sure dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(getString(R.string.DeleteTempStudentConfirmDialogTitle));
                alertDialogBuilder.setMessage(getString(R.string.DeleteTempStudentConfirm));
                alertDialogBuilder.setPositiveButton(getString(R.string.Delete), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //Remove student from list and mark for deletion
                        ((ActivityMain)getActivity()).removeName(thisStudent.GetId(), true);
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };

        btnDeleteTemp.setOnClickListener(btnDeleteTempOcl);

        //Create Info button listener
        ImageButton btnInfo = (ImageButton)rootView.findViewById(R.id.infoButton);

        View.OnClickListener btnInfoOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String infoBoxContent = String.format("%1$s", thisStudent.GetInfo());
                ((ActivityMain)getActivity()).openInfoBox(getString(R.string.InfoDialogTitle), infoBoxContent);
            }
        };

        btnInfo.setOnClickListener(btnInfoOcl);

        return rootView;
    }
}
