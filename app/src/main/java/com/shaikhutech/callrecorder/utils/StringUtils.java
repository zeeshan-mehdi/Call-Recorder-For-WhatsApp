package com.shaikhutech.callrecorder.utils;

import android.content.Context;

public class StringUtils  {
    public static  String prepareContacts(Context ctx,String number){
            if(number!=null && !number.isEmpty()){
                String preparednumbers=number.trim();
                preparednumbers=preparednumbers.replace(" ","");
                preparednumbers=preparednumbers.replace("(","");
                preparednumbers=preparednumbers.replace(")","");
                if(preparednumbers.contains("+")){
                    preparednumbers=preparednumbers.replace(preparednumbers.substring(0,3),""); //to remove country code
                }
                preparednumbers=preparednumbers.replace("-","");
                return preparednumbers;
            }else{
                return "Unknown";
            }


    }
}
