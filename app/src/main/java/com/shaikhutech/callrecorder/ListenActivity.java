package com.shaikhutech.callrecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;

import com.google.android.gms.ads.AdView;
import com.shaikhutech.callrecorder.contacts.ContactProvider;

public class ListenActivity extends AppCompatActivity {
    String number;
    ArrayList<String> recordinglist = new ArrayList<>();
    ArrayAdapter adapter;
    ArrayList<String> listen = new ArrayList<>();
    ArrayList<String> tracks = new ArrayList<>();
    ListView listView;
    private AdView mAdView;
    public static boolean mIsDestroying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        number = getIntent().getStringExtra("NUMBER");
        Toolbar toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(number + "");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String path = ContactProvider.getFolderPath(this);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tracks);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        File listfiles[] = file.listFiles();
        if (listfiles != null) {
            for (File list : listfiles) {
                recordinglist.add(list.getName());
            }
        }
        if (!recordinglist.isEmpty()) {
            int temp = 0;
            for (String s : recordinglist) {
                String numb[] = s.split("__");
                if (number.equals(numb[0])) {
                    ++temp;
                    listen.add(s);
                    tracks.add("Recording" + temp);
                }
            }
            adapter.notifyDataSetChanged();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Constant.sFROM_LISTEN_TO_MAIN = true;
                Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
                ContactProvider.playmusic(getApplicationContext(), ContactProvider.getFolderPath(getApplicationContext()) + "/" + listen.get(i), listen.get(i));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ListenActivity.class.getSimpleName(), " on Pause");
        mIsDestroying = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), true);
        if (Main2Activity.mIsDestroying) {
            Constant.sIS_FROM_ANOTHER_ACTIVITY = false;
            Main2Activity.mIsDestroying = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ListenActivity.class.getSimpleName(), " on Resume" + " isFrom another activity "
                + Constant.sIS_FROM_ANOTHER_ACTIVITY +
                " bckg status " + SharedPreferenceUtility.getBackgroundStatus(getApplicationContext()));
        if (!Constant.sFROM_FAV_TO_LISTEN) {
            if (SharedPreferenceUtility.getLockActivatedStatus(getApplicationContext())) {
                if ((SharedPreferenceUtility.getBackgroundStatus(getApplicationContext())) && (!(Constant.sIS_FROM_ANOTHER_ACTIVITY))) {
                    Constant.sIS_FROM_BACKGROUND = true;

                    boolean Auth=getIntent().getBooleanExtra("AUTH",false);
                    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
                    SharedPreferences SP1= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    boolean b1=SP1.getBoolean("LOCK",false);
                    if(b1&&!Auth){
                        if (ManageLockType.getLockType().equals("pin")){
                            Intent i = new Intent(getApplicationContext(),EnterNormalPIN.class);
                            i.putExtra("main","true");
                            finish();
                            startActivity(i);
                        }
                        if (ManageLockType.getLockType().equals("pattern")){
                            Intent i = new Intent(getApplicationContext(),EnterPatternLock.class);
                            i.putExtra("main","true");
                            finish();
                            startActivity(i);
                        }
                    }


                }
            }
        }
        Constant.sFROM_FAV_TO_LISTEN = false;
        Constant.sIS_FROM_ANOTHER_ACTIVITY = false;
        mIsDestroying = false;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mIsDestroying=false;
        Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
                SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
