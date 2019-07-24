package com.shaikhutech.callrecorder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import com.shaikhutech.callrecorder.locker.SetLockTypeActivity;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class SettingsActivity extends AppCompatPreferenceActivity {

    static Context ctx;
    public static boolean mIsDestroying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.content_settings);
        ctx = this;
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        EditTextPreference editTextPreference;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            Preference changepasswordd = findPreference("changepassword");
            changepasswordd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent=new Intent(getActivity(),SetLockTypeActivity.class);
                    intent.putExtra("SET",true);
                    startActivity(intent);
                    return true;
                }
            });


            Preference button = findPreference("DIRECTORY");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent chooserIntent = new Intent(ctx, DirectoryChooserActivity.class);
                    DirectoryChooserConfig config = DirectoryChooserConfig.builder().newDirectoryName("CallRecorder")
                            .allowNewDirectoryNameModification(true)
                            .build();
                    chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
                    startActivityForResult(chooserIntent, 1001);
                    return true;
                }
            });

            Preference buttonUNLIMITED = findPreference("UNLIMITED");
            buttonUNLIMITED.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isUnlimited = (Boolean) o;
                    if (isUnlimited) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                        alert.setTitle("Upgrade to pro");
                        alert.setMessage("Unlimited call recording feature available in pro version only. Do you want to upgrade? ");
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.callrecorder.procallrecorder")));
                                } catch (Exception e) {
                                    Toast.makeText(ctx, "Play store not found.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                    return false;
                }
            });
            final Preference savingOptions = (ListPreference) findPreference(getString(R.string.shared_pref_saving_pref_key));
           /* savingOptions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences pref = ctx.getSharedPreferences("TOGGLE", MODE_PRIVATE);
                    boolean sie = pref.getBoolean("STATE", true);
                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
                    // int index = Integer.parseInt(SP.getString(ctx.getString(R.string.shared_pref_saving_pref_key), "0"));
                    SharedPreferences.Editor ed = SP.edit();
                    if (sie) {
                        ed.putString(ctx.getString(R.string.shared_pref_saving_pref_key), "0");
                    } else {
                        ed.putString(ctx.getString(R.string.shared_pref_saving_pref_key), "1");
                    }
                    ed.apply();
                    return true;
                }
            });*/
            savingOptions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().equals("1")) {
                        MainActivity.mMainActivityInstance.setRecorderState(false);
                    } else {
                        MainActivity.mMainActivityInstance.setRecorderState(true);
                    }

                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1001) {
                if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                    SharedPreferences filepreference = ctx.getSharedPreferences("DIRECTORY", MODE_PRIVATE);
                    SharedPreferences.Editor editor = filepreference.edit();
                    editor.putString("DIR", data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                    editor.apply();
                    Intent intent = new Intent(ctx, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Nothing selected
                }
            }
        }
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

        Constant.sIS_FROM_ANOTHER_ACTIVITY = false;
        mIsDestroying = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }

}
