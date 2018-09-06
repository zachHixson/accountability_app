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

public class NameList_Add_RecyclerViewAdapter extends NameList_RecyclerViewAdapter {

    public NameList_Add_RecyclerViewAdapter(StudentList inpStudentList, Context inpContext){
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
            if (curStudent.GetRoom().equals("") && !curStudent.IsMarkedForDeletion()){
                this.students.add(curStudent);
            }
        }

        notifyDataSetChanged();
    }

    private class AddViewHolder extends ViewHolder{
        private ImageButton addBtn;

        private AddViewHolder(View v){
            super(v);

            //Disable rem button
            v.findViewById(R.id.removeButton).setVisibility(View.GONE);

            //Set addBtn onClick Listener
            addBtn = (ImageButton) v.findViewById(R.id.addButton);

            addBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    addRemBtnOnClick(v);
                }
            });
        }

        private void addRemBtnOnClick(View v) {
            int aptPos = getAdapterPosition();
            int id = this.getStudents().get(aptPos).GetId();

            if (!students.get(aptPos).IsSelected()){
                students.get(aptPos).SetSelected(true);
                ((Interface_ListEvents) context).selectName(id);
            }
            else{
                students.get(aptPos).SetSelected(false);
                ((Interface_ListEvents) context).deselectName(id);
            }

            notifyItemChanged(getAdapterPosition());
        }
    }
}
