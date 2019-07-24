package com.shaikhutech.callrecorder.locker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shaikhutech.callrecorder.ManageLockType;
import com.shaikhutech.callrecorder.R;


public class SetNormalPIN extends AppCompatActivity {

    EditText enter_pin,confirm_pin;
    Button set_pin;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.set_normal_pin);

        enter_pin = (EditText) findViewById(R.id.setPIN);
        confirm_pin = (EditText) findViewById(R.id.confirm);
        set_pin = (Button) findViewById(R.id.setNormalPIN);

        set_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_pin.getText().toString().equals(confirm_pin.getText().toString())){
                    PINManager.setPIN(enter_pin.getText().toString(),SetNormalPIN.this);
                    ManageLockType.setLockType("pin",SetNormalPIN.this);
                    finish();
                }
                if (!enter_pin.getText().toString().equals(confirm_pin.getText().toString())){
                    Toast.makeText(SetNormalPIN.this,getString(R.string.not_matched),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
