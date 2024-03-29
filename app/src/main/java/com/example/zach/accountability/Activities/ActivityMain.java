package com.example.zach.accountability.Activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;

import com.example.zach.accountability.BuildConfig;
import com.example.zach.accountability.Data_Structures.GlobalStates;
import com.example.zach.accountability.Data_Structures.Settings;
import com.example.zach.accountability.Data_Structures.StudentList;
import com.example.zach.accountability.Fragments.Fragment_TopBar;
import com.example.zach.accountability.IO.FileIO;
import com.example.zach.accountability.Interfaces.Interface_ListEvents;
import com.example.zach.accountability.Interfaces.Interface_MainListEvents;
import com.example.zach.accountability.Misc.DialogCreator;
import com.example.zach.accountability.NameList_RecyclerViewAdapter.NameList_Rem_RecyclerViewAdapter;
import com.example.zach.accountability.R;

public class ActivityMain extends AppCompatActivity implements Interface_ListEvents, Interface_MainListEvents {
    private final int ACTIVITY_ADD_STUDENT = 1;
    private final int ACTIVITY_OPEN_ROSTER = 2;

    ArrayList<String> actionHistory = new ArrayList<>();

    private NameList_Rem_RecyclerViewAdapter recycAdpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalStates.Settings = new Settings();
        GlobalStates.StudentList = new StudentList();
        setContentView(R.layout.activity_main);

        ImageButton btnAddStdnts = (ImageButton)findViewById(R.id.addButton);

        btnAddStdnts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openAddMenu();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
        updateRoomCount(GlobalStates.StudentList);
        GlobalStates.Settings.CurrentRoom = "main";
        initViewList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
        updateRoomCount(GlobalStates.StudentList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.optClearList:
                openClearListDialog();
                return true;
            case R.id.optAddTemp:
                openAddTempDialog();
                return true;
            case R.id.optOpen:
                openRosterDialog();
                return true;
            case R.id.optAbout:
                openAboutDialog();
                return true;
            case R.id.optHistoryButton:
                openHistoryDialog();
                return true;
            case R.id.optSort:
                openSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case ACTIVITY_ADD_STUDENT:
                //Make sure request was successful
                if(resultCode == RESULT_OK){
                    addStudentsToRoom(data.getIntegerArrayListExtra("idList"), GlobalStates.StudentList, GlobalStates.Settings.CurrentRoom);

                    ArrayList<Integer> delTempIds = data.getIntegerArrayListExtra("delTempIds");

                    //Remove and mark all names in delTempIds for deletion
                    for (int i = 0; i < delTempIds.size(); i++){
                        removeName(delTempIds.get(i), true);
                        updateRoomCount(GlobalStates.StudentList);
                    }

                    recycAdpt.updateFilter(GlobalStates.StudentList);
                }
                break;
            case ACTIVITY_OPEN_ROSTER:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    FileIO fileIO = new FileIO(this);
                    String rawJSON = fileIO.OpenContentUri(uri);

                    if (rawJSON != null) {
                        GlobalStates.StudentList.DeleteStoredList();
                        GlobalStates.StudentList.PopulateFromJSONString(rawJSON);
                        saveData();
                    }
                }
                break;
        }
    }

    public void initViewList(){
        RecyclerView studentRecycView;
        RecyclerView.LayoutManager   recycLayM;

        studentRecycView = (RecyclerView) findViewById(R.id.mainList_RecyView);
        recycLayM = new LinearLayoutManager(this);
        recycAdpt = new NameList_Rem_RecyclerViewAdapter(GlobalStates.StudentList, this);

        studentRecycView.setLayoutManager(recycLayM);
        studentRecycView.setAdapter(recycAdpt);
    }

    public void loadData(){
        FileIO fileIO = new FileIO(this);
        String rawJSON;

        //Load Settings and Data
        GlobalStates.Settings.Load(this);
        GlobalStates.CurrentRoom = GlobalStates.Settings.CurrentRoom;

        //Load Previous Roster
        rawJSON = fileIO.OpenLocalFile(GlobalStates.Settings.LocalRosterName);

        if (rawJSON != null){
            GlobalStates.StudentList.DeleteStoredList();
            GlobalStates.StudentList.PopulateFromJSONString(rawJSON);
            GlobalStates.StudentList.Sort(GlobalStates.Settings.sortByFirst);
            saveData();
        }
    }

    public void saveData(){
        //Declare variables
        FileIO fileIO = new FileIO(this);

        //Save the student list when the app is exited
        GlobalStates.StudentList.Save(this, GlobalStates.Settings.LocalRosterName);

        //Save Settings
        GlobalStates.Settings.Save(this);
    }

    public boolean selectName(int _id){
        return true;
    }

    public boolean deselectName(int _id){
        return true;
    }

    //Removes name from list (calls function in sub-fragments to do the same)
    public boolean removeName(int _id, boolean _markForDeletion){
        //Find which student has the id
        for (int i = 0; i < GlobalStates.StudentList.Size(); i++){
            if (GlobalStates.StudentList.GetStudent(i).GetId() == _id){
                GlobalStates.StudentList.GetStudent(i).SetRoom("");

                //if the student is temporary, and removed by Red X then mark for deletion
                if (_markForDeletion){
                    GlobalStates.StudentList.GetStudent(i).MarkForDeletion();
                }

                updateRoomCount(GlobalStates.StudentList);

                //Add action to history
                addToHistory(this.getString(R.string.HistoryRemoved), String.format("%1$s %2$s", GlobalStates.StudentList.GetStudent(i).GetFirstName(), GlobalStates.StudentList.GetStudent(i).GetLastName()));
            }
        }

        saveData();
        recycAdpt.updateFilter(GlobalStates.StudentList);

        return true;
    }

    public boolean addStudentsToRoom(ArrayList<Integer> _id, StudentList _studentList, String _currentRoom){
        //Declare variables
        String historyContent = "";
        int historySteps = 0;

        //Iterate through ids
        for (int ids = 0; ids < _id.size(); ids++){
            _studentList.GetStudent(_id.get(ids)).SetRoom(_currentRoom);

            //Add student to room
            updateRoomCount(GlobalStates.StudentList);
            if (historySteps == 0) {
                historyContent += String.format("%1$s %2$s", _studentList.GetStudent(_id.get(ids)).GetFirstName(), _studentList.GetStudent(_id.get(ids)).GetLastName());
            }
            historySteps += 1;
        }

        //Add action to history
        if (historySteps > 0) { //If there was more than one name added, display in format: Suzie LastName and 32 more
            if (historySteps > 1) {
                historySteps -= 1;
                historyContent = String.format("%1$s and %2$d more", historyContent, historySteps);
            }

            addToHistory(this.getString(R.string.HistoryAdded), historyContent);
        }

        saveData();

        return true;
    }

    public boolean updateRoomCount(StudentList inpStudentList){
        GlobalStates.Settings.RoomCount = 0;

        for (int i = 0; i < inpStudentList.Size(); i++){
            if (inpStudentList.GetStudent(i).IsAdded()){
                GlobalStates.Settings.RoomCount++;
            }
        }

        //Call method in TopBar sub-fragment
        Fragment_TopBar topBar = (Fragment_TopBar)getSupportFragmentManager().findFragmentById(R.id.topBar);
        Log.d("prnt", Integer.toString(GlobalStates.Settings.RoomCount));
        topBar.updateRoomCount(GlobalStates.Settings.RoomCount);

        return true;
    }

    //Add Item to history
    public boolean addToHistory(String _action, String _text){
        //If adding an item to history would exceed the max amount of history items, then delete the last one before adding
        if ((actionHistory.size() + 1) > GlobalStates.Settings.MaxHistorySteps){
            actionHistory.remove(actionHistory.size() - 1);
        }

        //Add new item to list
        String actionItem = String.format("%1$s: %2$s", _action, _text);
        actionHistory.add(0, actionItem);

        return true;
    }

    public boolean openAddMenu(){
        Intent intent = new Intent(ActivityMain.this, ActivityAddStudent.class);

        startActivityForResult(intent, ACTIVITY_ADD_STUDENT);

        return true;
    }

    public boolean openInfoBox(String _title, String _text){
        //Open DialogCreator box
        DialogCreator dialogCreator = new DialogCreator(ActivityMain.this);
        dialogCreator.CreateSimpleAlert(_title, _text.replace("; ", "\n"));

        return true;
    }

    private void openRosterDialog(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");

        startActivityForResult(intent, ACTIVITY_OPEN_ROSTER);
    }

    private void openClearListDialog(){
        if (GlobalStates.Settings.RoomCount > 0) {
            AlertDialog.Builder alertDialogBuilder =  new AlertDialog.Builder(ActivityMain.this);
            AlertDialog alertDialog;

            alertDialogBuilder.setTitle(this.getString(R.string.Warning));
            alertDialogBuilder.setMessage(this.getString(R.string.ClearListConfirm));
            alertDialogBuilder.setPositiveButton(this.getString(R.string.Ok), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //Get the nameList fragment
                    GlobalStates.StudentList.ClearList();
                    updateRoomCount(GlobalStates.StudentList);
                    recycAdpt.updateFilter(GlobalStates.StudentList);
                }
            });
            alertDialogBuilder.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //Exit the dialog
                    dialog.cancel();
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void openAddTempDialog(){
        AlertDialog.Builder alertDialogBuilder =  new AlertDialog.Builder(ActivityMain.this);
        AlertDialog alertDialog;

        //Declare inflater
        LayoutInflater inflater = LayoutInflater.from(ActivityMain.this);
        View promptView = inflater.inflate(R.layout.dialog_add_temp, null);

        alertDialogBuilder.setView(promptView);

        //Set title
        alertDialogBuilder.setTitle(this.getString(R.string.AddTempStudentDialogTitle));

        final EditText txtFName = (EditText)promptView.findViewById(R.id.addTempFName);
        final EditText txtLName = (EditText)promptView.findViewById(R.id.addTempLName);

        //Set contents
        alertDialogBuilder.setPositiveButton(this.getString(R.string.Add), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                String fName = txtFName.getText().toString();
                String lName = txtLName.getText().toString();

                GlobalStates.StudentList.AddTemp(fName, lName, GlobalStates.Settings.CurrentRoom);
                ArrayList<Integer> singleId = new ArrayList<Integer>();
                singleId.add(GlobalStates.StudentList.Size() - 1);
                addStudentsToRoom(singleId, GlobalStates.StudentList, GlobalStates.Settings.CurrentRoom);
                GlobalStates.StudentList.Sort(GlobalStates.Settings.sortByFirst);
                GlobalStates.StudentList.RefreshIds();
                recycAdpt.updateFilter(GlobalStates.StudentList);
            }
        });
        alertDialogBuilder.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });

        //Create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openAboutDialog(){
        //Set box contents
        String contents = String.format(
                "%1$s\n%2$s\n%3$s",
                this.getString(R.string.AboutCreator),
                this.getString(R.string.AboutYear),
                BuildConfig.VERSION_NAME
        );

        //Launch info box
        DialogCreator dialogCreator = new DialogCreator(ActivityMain.this);
        dialogCreator.CreateSimpleAlert(this.getString(R.string.AboutDialogTitle), contents);
    }

    public void openHistoryDialog(){
        String boxContents = "";

        //Set box contents
        for (int i = 0; i < actionHistory.size(); i++){
            boxContents += String.format("%1$s \n", actionHistory.get(i));
        }

        //Open History box
        openInfoBox(this.getString(R.string.HistoryDialogTitle), boxContents);
    }

    public void openSortDialog(){
        int checkedOption = 0;
        AlertDialog.Builder alertDialogBuilder;

        if (!GlobalStates.Settings.sortByFirst){
            checkedOption = 1;
        }

        alertDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
        alertDialogBuilder.setTitle(R.string.SortBy);
        alertDialogBuilder.setSingleChoiceItems(R.array.SortBy, checkedOption,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        GlobalStates.Settings.sortByFirst = (which == 0);
                    }
                }
        );
        alertDialogBuilder.setPositiveButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlobalStates.StudentList.Sort(GlobalStates.Settings.sortByFirst);
                recycAdpt.updateFilter(GlobalStates.StudentList);
            }
        });
        alertDialogBuilder.show();
    }
}
