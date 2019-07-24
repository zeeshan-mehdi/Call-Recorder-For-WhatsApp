package com.shaikhutech.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amnix.materiallockview.MaterialLockView;
import com.shaikhutech.callrecorder.locker.SaveLogs;
import com.shaikhutech.callrecorder.locker.SaveState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
public class EnterPatternLock extends AppCompatActivity {
    public String getPattern() {
        String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        String Shine = "/data/data/";
        String Url = "/files/pattern";
        File file = new File(Shine+PACKAGE_NAME+Url);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            Log.d("okuma hatası", "no 1");
        }
        return text.toString();
    }
    MaterialLockView materialLockView;
    Context context;
    RelativeLayout re;
    String app = null;
    TextView appNamePattern;
    ImageView appIconPattern;
    SaveState saveState;
    String main;
    boolean abc;
    SaveLogs saveLogs;
    int WRONG_PATTERN_COUNTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_pattern_lock);
        context = EnterPatternLock.this;
        Intent iin = getIntent();

        Bundle b = iin.getExtras();
        app = b.getString("app");
        try {
            main = b.getString("main");
            abc = main.equals("true");
        } catch (Exception e) {

        }
        saveLogs = new SaveLogs(this);

        appIconPattern = (ImageView) findViewById(R.id.appIconPattern);
        appNamePattern = (TextView) findViewById(R.id.appNamePattern);
        saveState = new SaveState(this);
        materialLockView = (MaterialLockView) findViewById(R.id.enterPatternLockView);
        re = (RelativeLayout) findViewById(R.id.re);
        materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                super.onPatternStart();
            }

            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                super.onPatternDetected(pattern, SimplePattern);
                if (getPattern().equals(SimplePattern)) {
                    if (abc) {
                        saveLogs.saveLogs(getString(R.string.app_name),true);
                        startMain();
                    } else {
                        saveState.saveState("false");
                        saveLogs.saveLogs(app,true);
                        startApp(app);
                        finish();
                    }
                } else {
                    materialLockView.clearPattern();
                    saveLogs.saveLogs(app,false);
                    WRONG_PATTERN_COUNTER = WRONG_PATTERN_COUNTER + 1;
                    if (WRONG_PATTERN_COUNTER == 3) {
                        finish();
                    }
                    Toast.makeText(context, getString(R.string.wrong_pattern), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    private void startMain() {
        Intent i = new Intent(EnterPatternLock.this, MainActivity.class);
        i.putExtra("AUTH",true);
        finish();
        startActivity(i);
    }
    @Override
    public void onBackPressed() {

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            Log.d("Focus debug", "Lost focus !");

        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private void startApp(String packagename) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packagename);
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

}
