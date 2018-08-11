package com.example.zach.accountability;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import com.example.zach.accountability.R;

public class DialogCreator {
    private Context Context;

    public DialogCreator(Context _context){
        this.Context = _context;
    }

    public void CreateSimpleAlert(String _title, String _contents){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.Context);
        alertDialogBuilder.setTitle(_title);
        alertDialogBuilder.setMessage(_contents);
        alertDialogBuilder.setPositiveButton(this.Context.getString(R.string.Close), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i){
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
