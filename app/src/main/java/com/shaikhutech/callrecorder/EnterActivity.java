package com.shaikhutech.callrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.shaikhutech.callrecorder.locker.SetLockTypeActivity;


public class EnterActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

            if (ManageLockType.getLockType().equals("")){
                Intent i = new Intent(EnterActivity.this,SetLockTypeActivity.class);
                startActivity(i);
                finish();
            }
            if (ManageLockType.getLockType().equals("pin")){
                Intent i = new Intent(EnterActivity.this,EnterNormalPIN.class);
                i.putExtra("main","true");
                startActivity(i);
                finish();
            }
            if (ManageLockType.getLockType().equals("pattern")){
                Intent i = new Intent(EnterActivity.this,EnterPatternLock.class);
                i.putExtra("main","true");
                startActivity(i);
                finish();
            }

    }
}
