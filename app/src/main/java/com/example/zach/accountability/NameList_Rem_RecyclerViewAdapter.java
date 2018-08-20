package com.example.zach.accountability;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;

public class NameList_Rem_RecyclerViewAdapter extends NameList_RecyclerViewAdapter {

    public NameList_Rem_RecyclerViewAdapter(StudentList inpStudentList, Context inpContext){
        super(inpStudentList, inpContext);
    }

    public NameList_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.fragment_student, parent, false);

        return new AddViewHolder(v);
    }

    private class AddViewHolder extends ViewHolder{
        private  ImageButton remBtn;

        private AddViewHolder(View v){
            super(v);

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

            if (v.getId() == remBtn.getId()){
                ((Interface_ListEvents)context).removeName(id, isMrkdForDel);
            }
        }
    }
}
