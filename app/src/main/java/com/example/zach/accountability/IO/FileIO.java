package com.example.zach.accountability.IO;

import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;

/*
  Class handles reading and writing basic android files
*/

public class FileIO {
    private Context ctx;

    public FileIO(Context _ctx){
        this.ctx = _ctx;
    }

    public String OpenLocalFile(String _filename){
        File file = new File(ctx.getFilesDir(), _filename);
        String returnString = null;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            returnString = new String(buffer, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return returnString;
    }

    public String OpenContentUri(Uri uri){
        try{
            InputStream inputStream = ctx.getContentResolver().openInputStream(uri);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void SaveLocalFile(String _fileName, String _contents){
        try{
            FileOutputStream outputStream;
            outputStream = ctx.openFileOutput(_fileName, Context.MODE_PRIVATE);
            outputStream.write(_contents.getBytes());
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
