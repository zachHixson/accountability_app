package com.example.zach.accountability;

import android.content.DialogInterface;
import android.provider.*;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import android.util.Log;

public class ActivityAddStudent extends AppCompatActivity {
    //Set Class variables
    StudentList studentList = GlobalStates.StudentList;
    ArrayList<Integer> idList = new ArrayList<>();
    ArrayList<Integer> delTempIds = new ArrayList<>();
    String currentRoom = GlobalStates.CurrentRoom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Prepare window
        setContentView(R.layout.activity_add_student);
        this.setFinishOnTouchOutside(false);

        //Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addStudentToolBar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                switch (item.getItemId()){
                    case R.id.addOptSelectAll:
                        //iterate through students and select them
                        for (int i = 0; i < studentList.Size(); i++){
                            //Make sure student is not already in room (therefor not in the list and will return a null
                            if (!studentList.GetStudent(i).GetRoom().equals(currentRoom)) {
                                Fragment_AddStudent addStudent = (Fragment_AddStudent) getSupportFragmentManager().findFragmentByTag(Integer.toString(i));
                                addStudent.selectSelf();
                            }
                        }
                        return true;
                    case R.id.addOptDeselectAll:
                        for (int i = 0; i < studentList.Size(); i++){
                            //Make sure student is not already in room (therefor not in the list and will return a null
                            if (!studentList.GetStudent(i).GetRoom().equals(currentRoom)) {
                                Fragment_AddStudent addStudent = (Fragment_AddStudent) getSupportFragmentManager().findFragmentByTag(Integer.toString(i));
                                addStudent.deselectSelf();
                            }
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });
        toolbar.inflateMenu(R.menu.add_student_menu);
        toolbar.setTitle(getString(R.string.AddStudentDialogTitle));

        //Fill List
        fillList(studentList);

        //Create cancel button listener
        Button btnCancel = (Button)findViewById(R.id.addCancel);

        View.OnClickListener btnCancelOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Make sure they want to cancel
                if (idList.size() > 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAddStudent.this);
                    alertDialogBuilder.setTitle(getString(R.string.AreYouSure));
                    alertDialogBuilder.setMessage(getString(R.string.ClearListConfirm));
                    alertDialogBuilder.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            //Prepare data for sending back to Main activity
                            Intent intent = new Intent();
                            intent.putExtra("idList", new ArrayList<Integer>()); //Because the user clicked cancel, we are sending back an empty list
                            intent.putExtra("delTempIds", delTempIds);
                            setResult(RESULT_OK, intent);

                            //Finish and close activity
                            finish();
                        }
                    });
                    alertDialogBuilder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else {
                    //Prepare data for sending back to Main activity
                    Intent intent = new Intent();
                    intent.putExtra("idList", new ArrayList<Integer>()); //Because the user clicked cancel, we are sending back an empty list
                    intent.putExtra("delTempIds", delTempIds);
                    setResult(RESULT_OK, intent);

                    //Finish and close activity
                    finish();
                }
            }
        };

        btnCancel.setOnClickListener(btnCancelOcl);

        //Create OK button listener
        Button btnOk = (Button)findViewById(R.id.addConfirm);

        View.OnClickListener btnOkOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Prepare data for sending back to Main activity
                Intent intent = new Intent();
                intent.putExtra("idList", idList);
                intent.putExtra("delTempIds", delTempIds);
                setResult(RESULT_OK, intent);

                //Finish and close activity
                finish();
            }
        };

        btnOk.setOnClickListener(btnOkOcl);
    }

    public boolean fillList(StudentList _studentList){
        Student student;
        Bundle argBundle;

        //Begin fragment transaction
        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();

        //Add all names to container
        for (int i = 0; i < _studentList.Size(); i++){
            //Set current student info
            student = _studentList.GetStudent(i);

            if (!student.GetRoom().equals(currentRoom) && !student.IsMarkedForDeletion()){
                //Create new instance of fragment
                Fragment_AddStudent studentFrag = new Fragment_AddStudent();

                //Set arguments to pass down to fragment
                argBundle = new Bundle();
                argBundle.putInt("id", student.GetId());
                studentFrag.setArguments(argBundle);

                //add the fragment to listView
                fragTrans.add(R.id.addList, studentFrag, Integer.toString(student.GetId()));
                fragTrans.addToBackStack(null);
            }
        }

        //Commit changes
        fragTrans.commit();

        return true;
    }

    public boolean selectStudent(int _id){
        //Add student id to list
        idList.add(_id);
        return true;
    }

    public boolean deselectStudent(int _id){
        //remove student id from list
        for (int i = 0; i < idList.size(); i++){
            if (idList.get(i) == _id){
                idList.remove(i);
            }
        }

        return true;
    }

    public boolean deleteTemp(int _id) {
        //Get argument
        final int studentId = _id;

        //Display Alert dialog confirming deletion
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAddStudent.this);
        alertDialogBuilder.setTitle(getString(R.string.DeleteTempStudentConfirmDialogTitle));
        alertDialogBuilder.setMessage(getString(R.string.DeleteTempStudentConfirm));
        alertDialogBuilder.setPositiveButton(getString(R.string.Delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Remove student from list and mark for deletion
                delTempIds.add(studentId);
                removeName(studentId);
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Cancel Dialog
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return true;
    }

    public boolean removeName(int _id){
        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.remove(getSupportFragmentManager().findFragmentByTag(Integer.toString(_id)));
        fragTrans.commit();

        return true;
    }

    public boolean openInfoBox(String _text){
        //Open DialogCreator box
        DialogCreator dialogCreator = new DialogCreator(ActivityAddStudent.this);
        dialogCreator.CreateSimpleAlert(getString(R.string.InfoDialogTitle), _text.replace(";", "\n"));

        return true;
    }
}
