package com.example.zach.accountability;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import android.util.Log;

public abstract class NameList_RecyclerViewAdapter extends RecyclerView.Adapter<NameList_RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Student> students;
    public Context            context;

    public NameList_RecyclerViewAdapter(StudentList inpStudentList, Context inpContext){
        students = new ArrayList<>();
        context = inpContext;
        updateFilter(inpStudentList);
    }

    //replace template contents of view
    @Override
    public void onBindViewHolder(NameList_RecyclerViewAdapter.ViewHolder holder, int position) {
        // - Get element from dataset at this position
        // - Replace the contents of the view with that element position
        Student student = students.get(position);
        holder.fName.setText(student.GetFirstName());
        holder.lName.setText(student.GetLastName());

        //Hide appropriate buttons
        if (!student.IsTemporary()){
            holder.delTempBtn.setVisibility(View.GONE);
        }
        else{
            holder.delTempBtn.setVisibility(View.VISIBLE);
        }

        if (student.GetInfo().equals("")){
            holder.infoBtn.setVisibility(View.GONE);
        }
        else{
            holder.infoBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount(){
        return students.size();
    }

    public abstract NameList_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public void updateFilter(StudentList inpStudentList){
        this.students.clear();
        this.students = inpStudentList.GetAddedStudents();
        notifyDataSetChanged();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        private TextView    fName;
        private TextView    lName;
        public  ImageButton delTempBtn;
        public  ImageButton infoBtn;
        public  View        layout;

        public ViewHolder(View v){
            super(v);
            layout = v;

            fName = (TextView) v.findViewById(R.id.firstName);
            lName = (TextView) v.findViewById(R.id.lastName);
            delTempBtn = (ImageButton) v.findViewById(R.id.deleteTempButton);
            infoBtn = (ImageButton) v.findViewById(R.id.infoButton);

            delTempBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delTempOnClick(v);
                }
            });

            infoBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    infoBtnOnClick(v);
                }
            });
        }

        private void delTempOnClick(View v){
            //Add are you sure dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getString(R.string.DeleteTempStudentConfirmDialogTitle));
            alertDialogBuilder.setMessage(context.getString(R.string.DeleteTempStudentConfirm));
            alertDialogBuilder.setPositiveButton(context.getString(R.string.Delete), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //Remove student from list and mark for deletion
                    ((Interface_ListEvents)context).removeName(students.get(getAdapterPosition()).GetId(), true);
                }
            });
            alertDialogBuilder.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        private void infoBtnOnClick(View v){
            String infoBoxContent = String.format("%1$s", students.get(getAdapterPosition()).GetInfo());
            ((Interface_ListEvents)context).openInfoBox(context.getString(R.string.InfoDialogTitle), infoBoxContent);
        }

        public ArrayList<Student> getStudents(){
            return students;
        }
    }
}