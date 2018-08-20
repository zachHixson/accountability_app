package com.example.zach.accountability;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;

public class NameList_Add_RecyclerViewAdapter extends NameList_RecyclerViewAdapter {

    public NameList_Add_RecyclerViewAdapter(StudentList inpStudentList, Context inpContext){
        super(inpStudentList, inpContext);
    }

    public NameList_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.fragment_student, parent, false);

        return new AddViewHolder(v);
    }

    private class AddViewHolder extends ViewHolder{
        private  ImageButton addBtn;

        private AddViewHolder(View v){
            super(v);

            addBtn = (ImageButton) v.findViewById(R.id.removeButton);

            addBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    addRemBtnOnClick(v);
                }
            });
        }

        private void addRemBtnOnClick(View v){
            int     id           = getStudents().get(getAdapterPosition()).GetId();
            boolean isMrkdForDel = getStudents().get(getAdapterPosition()).IsMarkedForDeletion();

            if (v.getId() == addBtn.getId()){
                ((Interface_ListEvents)context).removeName(id, isMrkdForDel);
            }
        }
    }
}
