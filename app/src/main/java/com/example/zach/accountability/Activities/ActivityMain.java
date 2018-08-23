package com.example.zach.accountability.Activities;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;

import com.example.zach.accountability.Data_Structures.GlobalStates;
import com.example.zach.accountability.Data_Structures.Settings;
import com.example.zach.accountability.Data_Structures.StudentList;
import com.example.zach.accountability.Fragments.Fragment_NameList;
import com.example.zach.accountability.Fragments.Fragment_TopBar;
import com.example.zach.accountability.IO.FileIO;
import com.example.zach.accountability.Interfaces.Interface_ListEvents;
import com.example.zach.accountability.Interfaces.Interface_MainListEvents;
import com.example.zach.accountability.Misc.DialogCreator;
import com.example.zach.accountability.R;

public class ActivityMain extends AppCompatActivity implements Interface_ListEvents, Interface_MainListEvents {
    //Create the main student list
    Settings appInfo = new Settings();
    ArrayList<String> actionHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalStates.StudentList = new StudentList();
        loadData();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        appInfo.CurrentRoom = "main";
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Declare variables
        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alertDialog;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.optClearList:
                //Pop up "Are You Sure" dialog
                alertDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
                alertDialogBuilder.setTitle(this.getString(R.string.Warning));
                alertDialogBuilder.setMessage(this.getString(R.string.ClearListConfirm));
                alertDialogBuilder.setPositiveButton(this.getString(R.string.Ok), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //Get the nameList fragment
                        Fragment_NameList listFrag = (Fragment_NameList)getSupportFragmentManager().findFragmentById(R.id.nameListWrapper);
                        GlobalStates.StudentList.ClearList();
                        updateRoomCount(GlobalStates.StudentList);
                        listFrag.updateList(GlobalStates.StudentList);
                    }
                });
                alertDialogBuilder.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //Exit the dialog
                        dialog.cancel();
                    }
                });

                if (appInfo.RoomCount > 0) {
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                return true;
            case R.id.optAddTemp:
                //Declare inflater
                LayoutInflater inflater = LayoutInflater.from(ActivityMain.this);
                View promptView = inflater.inflate(R.layout.dialog_add_temp, null);

                alertDialogBuilder = new AlertDialog.Builder(this);
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

                        GlobalStates.StudentList.AddTemp(fName, lName, appInfo.CurrentRoom); // remove #########################
                        ArrayList<Integer> singleId = new ArrayList<Integer>();
                        singleId.add(GlobalStates.StudentList.Size() - 1);
                        addStudentsToRoom(singleId, GlobalStates.StudentList, appInfo.CurrentRoom);
                        updateScreenList(GlobalStates.StudentList, appInfo.CurrentRoom);
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

                return true;
            case R.id.optImportDownloads:
                GlobalStates.StudentList.DeleteStoredList();

                //Open Roster from the downloads  and save it to local storage
                String rawJSON = openRoster(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), true);
                GlobalStates.StudentList.PopulateFromJSONString(rawJSON);

                //Save local file
                saveData();
                return true;
            case R.id.optAbout:
                //Set box contents
                String contents = String.format(
                        "%1$s\n%2$s\n%3$s",
                        this.getString(R.string.AboutCreator),
                        this.getString(R.string.AboutYear),
                        this.getString(R.string.AboutVersion)
                );

                //Launch info box
                DialogCreator dialogCreator = new DialogCreator(ActivityMain.this);
                dialogCreator.CreateSimpleAlert(this.getString(R.string.AboutDialogTitle), contents);

                return true;
            case R.id.optHistoryButton:
                String boxContents = "";

                //Set box contents
                for (int i = 0; i < actionHistory.size(); i++){
                    boxContents += String.format("%1$s \n", actionHistory.get(i));
                }

                //Open History box
                openInfoBox(this.getString(R.string.HistoryDialogTitle), boxContents);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check which request we are responding to
        if (requestCode == 1){
            //Make sure request was successful
            if(resultCode == RESULT_OK){
                addStudentsToRoom(data.getIntegerArrayListExtra("idList"), GlobalStates.StudentList, appInfo.CurrentRoom);

                ArrayList<Integer> delTempIds = data.getIntegerArrayListExtra("delTempIds");

                //Remove and mark all names in delTempIds for deletion
                for (int i = 0; i < delTempIds.size(); i++){
                    removeName(delTempIds.get(i), true);
                    updateRoomCount(GlobalStates.StudentList);
                }

                //un-pause the list
                Fragment_NameList nameList = (Fragment_NameList)getSupportFragmentManager().findFragmentById(R.id.nameListWrapper);
                nameList.setPaused(false);

                updateScreenList(GlobalStates.StudentList, appInfo.CurrentRoom);
            }
        }
    }

    public void loadData(){
        FileIO fileIO = new FileIO(this);

        //Load Settings and Data
        try {
            String rawSettings = fileIO.OpenLocalFile(appInfo.LocalSettingsName);
            appInfo.LoadJSON(rawSettings);
            GlobalStates.CurrentRoom = appInfo.CurrentRoom;
            updateRoomCount(GlobalStates.StudentList);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Load Previous Roster
        try{
            String rawJSON = openRoster(appInfo.LocalRosterName, false);
            GlobalStates.StudentList.DeleteStoredList();
            GlobalStates.StudentList.PopulateFromJSONString(rawJSON);
            updateScreenList(GlobalStates.StudentList, appInfo.CurrentRoom);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Opens a roster from the specified path
    public String openRoster(String _path, boolean _external){
        String returnString = "";
        String fileName = appInfo.LocalRosterName;
        FileIO fileIO = new FileIO(this);

        //Check to make sure external storage exists
        if (_external && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File file = new File(_path, fileName); //Get full file path
            returnString = fileIO.OpenExternalFile(file);
        }
        else if (!_external){
            returnString = fileIO.OpenLocalFile(fileName);
        }

        return returnString;
    }

    public void saveData(){
        //Declare variables
        FileIO fileIO = new FileIO(this);

        //Save the student list when the app is exited
        fileIO.SaveLocalFile(appInfo.LocalRosterName, GlobalStates.StudentList.ToJSONString());

        //Save Settings
        fileIO.SaveLocalFile(appInfo.LocalSettingsName, appInfo.toString());
    }

    //Updates the list on the screen (calls function in sub-fragment)
    public void updateScreenList(StudentList _studentList, String _currentRoom){ //Updates the names on the screen
        //Get the nameList fragment
        Fragment_NameList listFrag = (Fragment_NameList)getSupportFragmentManager().findFragmentById(R.id.nameListWrapper);
        listFrag.updateList(_studentList);
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
        updateScreenList(GlobalStates.StudentList, appInfo.CurrentRoom);

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

    public String getRoom(){
        return appInfo.CurrentRoom;
    }

    public boolean updateRoomCount(StudentList inpStudentList){
        appInfo.RoomCount = 0;

        for (int i = 0; i < inpStudentList.Size(); i++){
            if (inpStudentList.GetStudent(i).IsAdded()){
                appInfo.RoomCount++;
            }
        }

        //Call method in TopBar sub-fragment
        Fragment_TopBar topBar = (Fragment_TopBar)getSupportFragmentManager().findFragmentById(R.id.topBar);
        topBar.updateRoomCount(appInfo.RoomCount);

        return true;
    }

    //Add Item to history
    public boolean addToHistory(String _action, String _text){
        //If adding an item to history would exceed the max amount of history items, then delete the last one before adding
        if ((actionHistory.size() + 1) > appInfo.MaxHistorySteps){
            actionHistory.remove(actionHistory.size() - 1);
        }

        //Add new item to list
        String actionItem = String.format("%1$s: %2$s", _action, _text);
        actionHistory.add(0, actionItem);

        return true;
    }

    public boolean openAddMenu(){
        Intent intent = new Intent(ActivityMain.this, ActivityAddStudent.class);

        startActivityForResult(intent, 1);

        return true;
    }

    public boolean openInfoBox(String _title, String _text){
        //Open DialogCreator box
        DialogCreator dialogCreator = new DialogCreator(ActivityMain.this);
        dialogCreator.CreateSimpleAlert(_title, _text);

        return true;
    }
}
