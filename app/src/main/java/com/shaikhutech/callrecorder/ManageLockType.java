package com.shaikhutech.callrecorder;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class ManageLockType {
    public static void setLockType(String lockType,Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("p", Context.MODE_PRIVATE);
            fileOutputStream.write(lockType.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {

        }
    }

    public static String getLockType(){
        String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        String Shine = "/data/data/";
        String Url = "/files/p";

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
            Log.d("okuma hatası","no 1");
        }

        return text.toString();
    }

}
