package com.shaikhutech.callrecorder.locker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.amnix.materiallockview.MaterialLockView;
import com.shaikhutech.callrecorder.MainActivity;
import com.shaikhutech.callrecorder.R;

import java.io.FileOutputStream;
import java.util.List;


public class SetPatternLockActivity extends AppCompatActivity {

    MaterialLockView materialLockView;
    Context context;
    String new_pattern;
    Button save_pattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_pattern_lock);
        Log.d("Set"," pattern");
        context = SetPatternLockActivity.this;
        save_pattern = (Button) findViewById(R.id.save_pattern_cont);
        materialLockView = (MaterialLockView) findViewById(R.id.patternLockView);
        materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                super.onPatternStart();

            }

            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                super.onPatternDetected(pattern, SimplePattern);
                new_pattern = SimplePattern;

            }
        });

        save_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPattern(new_pattern);
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void setPattern(String pattern){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("pattern", Context.MODE_PRIVATE);
            fileOutputStream.write(pattern.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {

        }
    }
}

