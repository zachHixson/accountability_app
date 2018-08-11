package com.example.zach.accountability;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

/*
  Class handles reading and writing basic android files
*/

public class FileIO {
    private Context ctx;

    public FileIO(Context _ctx){
        this.ctx = _ctx;
    }

    private String loadFile(File _file){
        String returnString = "";

        try {
            FileInputStream inputStream = new FileInputStream(_file);
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

    public String OpenLocalFile(String _filename){
        File file = new File(ctx.getFilesDir(), _filename);
        String returnString = loadFile(file);

        return returnString;
    }

    public String OpenExternalFile(File _file){
        String returnString = loadFile(_file);

        return returnString;
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
