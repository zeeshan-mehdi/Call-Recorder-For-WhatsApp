package com.shaikhutech.callrecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.io.IOException;

import static com.shaikhutech.callrecorder.contacts.ContactProvider.getFolderPath;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = "Main2Activity";

    FloatingActionButton buttonPlayPause;
    SeekBar seekBarProgress;
    MediaPlayer mediaPlayer;
    Handler seekHandler = new Handler();
    private int mediaFileLengthInMilliseconds;
    TextView title;
    String path;
    public static boolean mIsDestroying = false;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplayer);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        path = getIntent().getStringExtra("PATH");
        title = findViewById(R.id.name);
        title.setText(path);
        getInit();
    }

    public void getInit() {
        buttonPlayPause = (FloatingActionButton) findViewById(R.id.button1);
        seekBarProgress = (SeekBar) findViewById(R.id.seekBar2);
        seekBarProgress.setMax(99);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getFolderPath(this) + "/" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekUpdation();
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!mediaPlayer.isPlaying()) {
            buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        } else {
            buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        }
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                } else {
                    mediaPlayer.pause();
                    buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
                seekUpdation();
            }
        });
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!mediaPlayer.isPlaying()) {
                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * seekBarProgress.getProgress();
                    mediaPlayer.seekTo(playPositionInMillisecconds);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
                if (!mediaPlayer.isPlaying()) {
                    buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                } else {
                    buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
                seekUpdation();
            }
        });
    }

    private void seekUpdation() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    seekUpdation();
                }
            };
            seekHandler.postDelayed(notification, 1000);
        } else {
            buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsDestroying = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Constant.sFROM_LISTEN_TO_MAIN) {
            if (SharedPreferenceUtility.getLockActivatedStatus(getApplicationContext())) {
                if ((SharedPreferenceUtility.getBackgroundStatus(getApplicationContext()))
                        && (!(Constant.sIS_FROM_ANOTHER_ACTIVITY))) {
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
        Constant.sFROM_LISTEN_TO_MAIN = false;
        Constant.sIS_FROM_ANOTHER_ACTIVITY = false;
        mIsDestroying = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
        // mIsDestroying=false;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }
}
