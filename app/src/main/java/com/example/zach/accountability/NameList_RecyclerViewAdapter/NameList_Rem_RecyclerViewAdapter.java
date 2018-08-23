package com.example.zach.accountability.NameList_RecyclerViewAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import com.example.zach.accountability.Interfaces.Interface_ListEvents;
import com.example.zach.accountability.R;
import com.example.zach.accountability.Data_Structures.Student;
import com.example.zach.accountability.Data_Structures.StudentList;

public class NameList_Rem_RecyclerViewAdapter extends NameList_RecyclerViewAdapter {

    public NameList_Rem_RecyclerViewAdapter(StudentList inpStudentList, Context inpContext){
        super(inpStudentList, inpContext);
    }

    public NameList_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.student_tile, parent, false);

        return new AddViewHolder(v);
    }

    public void updateFilter(StudentList inpStudentList){
        this.students.clear();

        for (int i = 0; i < inpStudentList.Size(); i++){
            Student curStudent = inpStudentList.GetStudent(i);
            if (!curStudent.GetRoom().equals("")){
                this.students.add(curStudent);
            }
        }

        notifyDataSetChanged();
    }

    private class AddViewHolder extends ViewHolder{
        private ImageButton remBtn;

        private AddViewHolder(View v){
            super(v);

            //Disable add button
            v.findViewById(R.id.addButton).setVisibility(View.GONE);

            //Set remove button event listener
            remBtn = (ImageButton) v.findViewById(R.id.removeButton);

            remBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    addRemBtnOnClick(v);
                }
            });
        }

        private void addRemBtnOnClick(View v){
            int     id           = getStudents().get(getAdapterPosition()).GetId();
            boolean isMrkdForDel = getStudents().get(getAdapterPosition()).IsMarkedForDeletion();

            ((Interface_ListEvents)context).removeName(id, isMrkdForDel);
        }
    }
}
