package com.example.zach.accountability.Activities;

import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import com.example.zach.accountability.Data_Structures.GlobalStates;
import com.example.zach.accountability.Data_Structures.StudentList;
import com.example.zach.accountability.Interfaces.Interface_ListEvents;
import com.example.zach.accountability.Misc.DialogCreator;
import com.example.zach.accountability.NameList_RecyclerViewAdapter.NameList_Add_RecyclerViewAdapter;
import com.example.zach.accountability.R;

public class ActivityAddStudent extends AppCompatActivity implements Interface_ListEvents{
    //Set Class variables
    StudentList                      studentList = GlobalStates.StudentList;
    ArrayList<Integer>               idList = new ArrayList<>();
    ArrayList<Integer>               delTempIds = new ArrayList<>();
    String                           currentRoom = GlobalStates.CurrentRoom;
    NameList_Add_RecyclerViewAdapter recycAdpt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView                     studentRecycView;
        RecyclerView.LayoutManager       recycLayM;

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
                        selectAll(GlobalStates.StudentList);
                        recycAdpt.notifyDataSetChanged();
                        return true;
                    case R.id.addOptDeselectAll:
                        deselectAll(GlobalStates.StudentList);
                        recycAdpt.notifyDataSetChanged();
                        return true;
                    default:
                        return true;
                }
            }
        });
        toolbar.inflateMenu(R.menu.add_student_menu);
        toolbar.setTitle(getString(R.string.AddStudentDialogTitle));

        //Set up recycler viewer
        studentRecycView = (RecyclerView) findViewById(R.id.addList_RecyView);
        recycLayM = new LinearLayoutManager(this);
        recycAdpt = new NameList_Add_RecyclerViewAdapter(GlobalStates.StudentList, this);

        studentRecycView.setLayoutManager(recycLayM);
        studentRecycView.setAdapter(recycAdpt);

        //Create cancel button listener
        Button btnCancel = (Button)findViewById(R.id.addCancel);

        View.OnClickListener btnCancelOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activityFinishCancel();
            }
        };

        btnCancel.setOnClickListener(btnCancelOcl);

        //Create OK button listener
        Button btnOk = (Button)findViewById(R.id.addConfirm);

        View.OnClickListener btnOkOcl = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activityFinishOK();
            }
        };

        btnOk.setOnClickListener(btnOkOcl);
    }

    public boolean selectName(int _id){
        //Add student id to list
        idList.add(_id);
        return true;
    }

    public boolean selectAll(StudentList inpStudentList){
        inpStudentList.SetAllSelected(true);

        for (int i = 0; i < inpStudentList.Size(); i++){
            idList.add(inpStudentList.GetStudent(i).GetId());
        }

        return true;
    }

    public boolean deselectName(int _id){
        //remove student id from list
        for (int i = 0; i < idList.size(); i++){
            if (idList.get(i) == _id){
                idList.remove(i);
            }
        }

        return true;
    }

    public boolean deselectAll(StudentList inpStudentList){
        inpStudentList.SetAllSelected(false);
        idList.clear();

        return true;
    }

    public boolean removeName(int _id, boolean _markForDeletion){

        if (_markForDeletion) {
            GlobalStates.StudentList.GetStudent(_id).MarkForDeletion();
            deselectName(_id);
        }

        recycAdpt.updateFilter(GlobalStates.StudentList);

        return true;
    }

    public boolean openInfoBox(String _title, String _text){
        //Open DialogCreator box
        DialogCreator dialogCreator = new DialogCreator(ActivityAddStudent.this);
        dialogCreator.CreateSimpleAlert(getString(R.string.InfoDialogTitle), _text.replace(";", "\n"));

        return true;
    }

    public void activityFinishOK(){
        finishWithIntent(idList, delTempIds);
    }

    public void activityFinishCancel(){
        //Make sure they want to cancel
        if (idList.size() > 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAddStudent.this);
            alertDialogBuilder.setTitle(getString(R.string.AreYouSure));
            alertDialogBuilder.setMessage(getString(R.string.ClearListConfirm));
            alertDialogBuilder.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    finishWithIntent(new ArrayList<Integer>(), delTempIds);
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
            finishWithIntent(new ArrayList<Integer>(), delTempIds);
        }
    }

    public void finishWithIntent(ArrayList<Integer> inpIdList, ArrayList<Integer> inpDelTempIds){
        GlobalStates.StudentList.SetAllSelected(false);

        //Prepare data for sending back to Main activity
        Intent intent = new Intent();
        intent.putExtra("idList", inpIdList);
        intent.putExtra("delTempIds", inpDelTempIds);
        setResult(RESULT_OK, intent);

        //Finish and close activity
        finish();
    }
}
