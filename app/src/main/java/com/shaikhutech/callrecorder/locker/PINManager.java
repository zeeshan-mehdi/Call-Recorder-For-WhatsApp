package com.shaikhutech.callrecorder.locker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.shaikhutech.callrecorder.BuildConfig;
import com.shaikhutech.callrecorder.R;
import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.security.GeneralSecurityException;

public class PINManager {
    public static String AB = "qwEwfbfs";
    public static void setPIN(String PIN,Context context){
        try {
            String encryptedMsg = AESCrypt.encrypt(AB, PIN);
            FileOutputStream fileOutputStream = context.openFileOutput("pin", Context.MODE_PRIVATE);
            fileOutputStream.write(encryptedMsg.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(context, context.getString(R.string.pin_setted),Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }
    public static String getPIN(){

        String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        String Shine = "/data/data/";
        String Url = "/files/pin";

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
        catch (Exception e) {
            Log.d("okuma hatası","no 1");
        }
        String messageAfterDecrypt = null;
        try {
            messageAfterDecrypt = AESCrypt.decrypt(AB, text.toString());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return messageAfterDecrypt;
    }
}
