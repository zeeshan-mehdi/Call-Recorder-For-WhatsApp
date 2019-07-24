package com.shaikhutech.callrecorder.locker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.shaikhutech.callrecorder.ManageLockType;
import com.shaikhutech.callrecorder.R;


public class SetLockTypeActivity extends AppCompatActivity {

    SwitchCompat setPattern,setNormalPIN;
    Context context;
    Button start;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.set_lock_type);
        context = SetLockTypeActivity.this;
        setPattern = (SwitchCompat) findViewById(R.id.setPattern);
        setNormalPIN = (SwitchCompat) findViewById(R.id.setNormalPINSwitch);
        start = (Button) findViewById(R.id.strt);

        ManageLockType.setLockType("",context);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setPattern.isChecked() && !setNormalPIN.isChecked()){
                    startPattern();
                }
                if (!setPattern.isChecked()&& setNormalPIN.isChecked()){
                    startSetNormalPIN();
                }
                if (!setPattern.isChecked() && !setNormalPIN.isChecked()){
                    Toast.makeText(context,getString(R.string.select_lock_type),Toast.LENGTH_SHORT).show();
                }
            }
        });
        setNormalPIN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    ManageLockType.setLockType("pin",context);
                    setPattern.setChecked(false);
                }
                if (!b){
                    ManageLockType.setLockType("tp",context);
                    setPattern.setChecked(true);
                }
            }
        });

        setPattern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    ManageLockType.setLockType("pattern",context);
                    setNormalPIN.setChecked(false);
                }
                if (!b){
                    ManageLockType.setLockType("tp",context);
                    setNormalPIN.setChecked(true);
                }
            }
        });
    }

    private void startSetNormalPIN(){
        Intent i = new Intent(SetLockTypeActivity.this,SetNormalPIN.class);
        i.putExtra("try","true");
        startActivity(i);
        finish();
    }
    private void startPattern(){
        Intent i = new Intent(SetLockTypeActivity.this,SetPatternLockActivity.class);
        startActivity(i);
        finish();
    }
}
