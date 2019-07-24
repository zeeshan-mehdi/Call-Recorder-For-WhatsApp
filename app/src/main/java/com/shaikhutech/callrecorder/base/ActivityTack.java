package com.shaikhutech.callrecorder.base;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

public class ActivityTack {
	
	
	public 	 List<Activity> activityList=new ArrayList<Activity>();
	
	public static ActivityTack tack=new ActivityTack();
	
	public static  ActivityTack getInstanse(){
		return tack;
	}
	
	private ActivityTack(){
		
	}
	
	public  void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	public  void removeActivity(Activity activity){
		activityList.remove(activity);
	}



}
