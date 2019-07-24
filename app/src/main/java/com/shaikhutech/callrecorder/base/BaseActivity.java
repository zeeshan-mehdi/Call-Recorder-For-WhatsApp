package com.shaikhutech.callrecorder.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;

import butterknife.ButterKnife;



public abstract class BaseActivity extends FragmentActivity {

	
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;
	protected Context mContext;
	protected String LogName;
	protected ActivityTack tack = ActivityTack.getInstanse();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mContext = this;
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
		LogName = this.getClass().getSimpleName();
		tack.addActivity(this);


	}

	@Override
	public void setContentView(int layoutResID) {
		
		super.setContentView(layoutResID);
		ButterKnife.inject(this);

	}

	
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}


	protected void startActivity(String action) {
		startActivity(action, null);
	}

	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}


	@Override
	public void finish() {
		super.finish();
		tack.removeActivity(this);

	}




    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
}
