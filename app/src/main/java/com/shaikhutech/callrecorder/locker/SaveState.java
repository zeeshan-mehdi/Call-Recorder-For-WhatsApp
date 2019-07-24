package com.shaikhutech.callrecorder.locker;

import android.content.Context;
import android.util.Log;

import com.shaikhutech.callrecorder.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class SaveState {
    Context context;
    public SaveState(Context context1){
        context = context1;
    }
    public boolean getState(){
        String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        String Shine = "/data/data/";
        String Url = "/files/state";

        File file = new File(Shine+PACKAGE_NAME+Url);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {

        }
        return text.toString().contains("true");
    }
    public void saveState(String name){
        try {
            FileOutputStream fOut = context.openFileOutput("state",  context.MODE_PRIVATE);
            fOut.write(name.getBytes());
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            Log.e("Hata kodu3", "File write failed: " + e.toString());
        }
    }
}
