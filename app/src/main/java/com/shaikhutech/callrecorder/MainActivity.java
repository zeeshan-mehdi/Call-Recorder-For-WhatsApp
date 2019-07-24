package com.shaikhutech.callrecorder;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shaikhutech.callrecorder.service.CallDetectionService;
import com.shaikhutech.callrecorder.utils.AppUtils;
import com.andrognito.flashbar.Flashbar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shaikhutech.callrecorder.SqliteDatabase.ContactsDatabase;
import com.shaikhutech.callrecorder.Transformer.ZoomOutPageTransformer;
import com.shaikhutech.callrecorder.adapter.ScreenSlidePagerAdapter;
import com.shaikhutech.callrecorder.contacts.ContactProvider;
import com.shaikhutech.callrecorder.fragments.Incomming;
import com.shaikhutech.callrecorder.fragments.Outgoing;
import com.shaikhutech.callrecorder.pojo_classes.Contacts;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private  ViewPager viewPager;


    //timer task

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();


    private Switch whatsToggle;

    SharedPreferences prefofsync;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RelativeLayout bg;
    private ScreenSlidePagerAdapter adapter;
    static refreshstener refreshlistenerobj;
    static querySearch queylistener;
    static querySearch2 queylistener2;
    ProgressDialog bar;
    static querySearch3 queylistener3;
    private SearchView mSearchView;
    public ActionMode mActionMode;
    private static String mTextToBeSearched = "";
    ArrayList<Contacts> phoneContacts=new ArrayList<>();
    ArrayList<String> recordinglist=new ArrayList<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2001;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Switch toggleRecord;

    Flashbar flashbar =null;

    Activity mActivity;
    private boolean startService = true;
    private boolean shouldLoad = true;

    private void askPermission() {

        try {

            ImageView gifImageView = new ImageView(getApplicationContext());

            AlertDialog.Builder share_dialog = new AlertDialog.Builder(MainActivity.this);
            share_dialog.setMessage("Enable Draw Over Apps Permission");
            share_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
                    dialogInterface.dismiss();
                }
            });
            share_dialog.setCancelable(false);
            share_dialog.setView(gifImageView);
            Picasso.with(getApplicationContext()).load("https://media.giphy.com/media/QW3wOhFfW45Cn4TEgv/giphy.gif").into(gifImageView);
            share_dialog.show();

            startTimer();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static MainActivity mMainActivityInstance;
    Incomming fr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        mActivity = this;
        mMainActivityInstance = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            checkAndRequestPermissions();
        }






//        if(isMyServiceRunning(ButtonService.class)){
//            Toast.makeText(mActivity, "starting", Toast.LENGTH_SHORT).show();
            //startService(new Intent(MainActivity.this,ButtonService.class));
//        }else{
//            Toast.makeText(mActivity, "Already running", Toast.LENGTH_SHORT).show();
//        }


        if(flashbar==null){
            flashbar = primaryActionListener();
        }


        prefofsync = getSharedPreferences("SYNC", MODE_PRIVATE);

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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startAnim();
        viewPager=findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        adapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout=findViewById(R.id.tabs);
        bg=findViewById(R.id.bg);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    changeColorOfStatusAndActionBar();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        final SharedPreferences pref = getSharedPreferences("TOGGLE", MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View myLayout = findViewById(R.id.header_layout_nav);
        toggleRecord = myLayout.findViewById(R.id.switch1);

        whatsToggle = myLayout.findViewById(R.id.switch2);

        boolean whats = pref.getBoolean("whats",true);

        if(whats){
            whatsToggle.setChecked(true);
            whatsToggle.setText("WhatsApp Recording On");

            if(!isMyServiceRunning(ButtonService.class)){
                startService = true;
            }else{
                startService = false;
            }

        }else{
            whatsToggle.setChecked(false);
            whatsToggle.setText("WhatsApp Recording off");
            if(isMyServiceRunning(ButtonService.class)){
                stopService(new Intent(MainActivity.this,ButtonService.class));
            }
        }


        boolean sie = pref.getBoolean("STATE", true);
        if (sie) {
            toggleRecord.setChecked(true);
            toggleRecord.setText("Call Recording On");
        } else {
            toggleRecord.setChecked(false);
            toggleRecord.setText("Call Recording Off");
        }

        whatsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(MainActivity.this, "status"+b, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = pref.edit();

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor ed = SP.edit();

                if (b) {
                    editor.putBoolean("whats", b);
                    editor.apply();
                    whatsToggle.setText("WhatsApp Recording On");
                    whatsToggle.setChecked(true);
                    startService = true;
                    startServices();
                } else {
                    editor.putBoolean("whats", b);
                    editor.apply();
                    startService = false;
                    stopService(new Intent(MainActivity.this,ButtonService.class));
                    whatsToggle.setText("WhatsApp Recording Off");
                    whatsToggle.setChecked(false);
                }
            }
        });


        toggleRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(MainActivity.this, "status"+b, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = pref.edit();

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor ed = SP.edit();

                if (b) {
                    editor.putBoolean("STATE", b);
                    editor.apply();
                    toggleRecord.setText("Call Recording On");
                    ed.putString(getString(R.string.shared_pref_saving_pref_key), "0");
                    ed.apply();
                } else {
                    editor.putBoolean("STATE", b);
                    editor.apply();
                    toggleRecord.setText("Call Recording Off");
                    ed.putString(getString(R.string.shared_pref_saving_pref_key), "1");
                    ed.apply();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        showlistfile();
        Intent intent = new Intent(MainActivity.this, CallDetectionService.class);
        startService(intent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {

            tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_002_incoming_phone_call_symbol));
            tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_001_outgoing_call));

        } else {

            tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_recvied));
            tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_outgoing));
        }

        admobbanner();


    }

    private boolean isUnlimitedRecordings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        return sharedPreferences.getBoolean("UNLIMITED", true);
    }


    private boolean storeToDatabase(ArrayList<Contacts> phoneContacts) {
        ContactsDatabase datbaseObj = new ContactsDatabase(this);
        for (Contacts con : phoneContacts) {
            //photo uri got here
            if (datbaseObj.isContact(con.getNumber()).getNumber() != null) {
                datbaseObj.updateContact(con);
            } else {
                datbaseObj.addContact(con);
            }
        }
        return true;
    }

    public void setRecorderState(boolean pStatus) {
        if (toggleRecord != null) {
            toggleRecord.setChecked(pStatus);
        }
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void showlistfile() {
        Bundle bundles=new Bundle();
        String path=ContactProvider.getFolderPath(this);
        File file=new File(path);
        if(!file.exists()){

            file.mkdirs();
        }
        File listfiles[]=file.listFiles();
        if(listfiles!=null){
            for(File list:listfiles){
                recordinglist.add(list.getName());
            }
        }
        bundles.putStringArrayList("RECORDING",recordinglist);

        fr=new Incomming();
        fr.setArguments(bundles);
        Outgoing outgoing=new Outgoing();
        outgoing.setArguments(bundles);

        adapter.addFrag(fr,"Incomming");
        adapter.addFrag(outgoing,"Outgoing");
        adapter.notifyDataSetChanged();
    }


    private Flashbar primaryActionListener() {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.TOP)
                .title("Audio Record")
                .message("click recording button to recording")
                .primaryActionText("Record")
                .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(Flashbar bar) {
                        bar.dismiss();
                    }
                })
                .build();
    }


    private void changeColorOfStatusAndActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
             switch (viewPager.getCurrentItem()) {
                case 0:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    window.setStatusBarColor(getResources().getColor(R.color.smooth_red_dark));
                    bg.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case 1:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    window.setStatusBarColor(getResources().getColor(R.color.smooth_red_dark));
                    bg.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                    break;
                default:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    window.setStatusBarColor(getResources().getColor(R.color.smooth_red_dark));
                    bg.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_resourse_file,menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTextToBeSearched = newText;
                    queylistener2.Search_name2(newText+"");
                try {
                   queylistener3.Search_name3(newText+"");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                if(!newText.isEmpty()){
                    tabLayout.setVisibility(View.GONE);
                }else{
                    tabLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        return true;
    }
    public static void fetchSearchRecords() {
        if (queylistener != null) {
            queylistener.Search_name(mTextToBeSearched);
        }
        if (queylistener2 != null) {
            queylistener2.Search_name2(mTextToBeSearched);
        }
        if (queylistener3 != null) {
            queylistener3.Search_name3(mTextToBeSearched);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.setting){
            Intent intent= new Intent(MainActivity.this,SettingsActivity2.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0){
           super.onBackPressed();
        }else{
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    private class AsyncAdapter1 extends AsyncTask<Void, Integer, ArrayList<Contacts>> {

        @Override
        protected void onPostExecute(ArrayList<Contacts> contactses) {
            refreshlistenerobj.refresh(true);
            if (fr.isAdded()) {
                fr.refresh(true);
            }


            if (prefofsync.getBoolean("RED", true)) {
                bar.dismiss();
                SharedPreferences.Editor editor = prefofsync.edit();
                editor.putBoolean("RED", false);
                editor.apply();
                addAppToProtectedMode();
            }

           /* if (prefofsync.getBoolean("RED", true)) {
                SharedPreferences.Editor editor = prefofsync.edit();
                editor.putBoolean("RED", false);
                editor.apply();
            }*/
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... voids) {
            ArrayList<Contacts> backphone = ContactProvider.getContacts(getApplicationContext());
            if (backphone != null) {
                storeToDatabase(backphone);
            }
            return backphone;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences prefofsync = getSharedPreferences("SYNC", MODE_PRIVATE);
            if (prefofsync.getBoolean("RED", true)) {
                bar.show();
            }

        }
    }

    private void addAppToProtectedMode() {
        if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !/*sp.getBoolean("protected",false)*/
                SharedPreferenceUtility.getProtectedModeStatus(getApplicationContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.protected_mode_title).setMessage(R.string.add_to_protected_mode)
                    .setPositiveButton(R.string.add_to_protected_mode_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager",
                                    "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
                            // sp.edit().putBoolean("protected",true).commit();
                            SharedPreferenceUtility.putProtectedModeStatus(getApplicationContext());
                        }
                    }).setNegativeButton(R.string.add_to_protected_mode_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferenceUtility.putProtectedModeStatus(getApplicationContext());
                }
            }).setCancelable(false).create().show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.canDrawOverlays(this)&&startService) {
                ButtonService.context = getApplicationContext();
                startService(new Intent(MainActivity.this, ButtonService.class));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttachedToWindow() {
        Log.e("main","executing");
        startServices();

        super.onAttachedToWindow();
    }


    public void startServices(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M&&startService) {
            ButtonService.context = getApplicationContext();
            startService(new Intent(MainActivity.this, ButtonService.class));

        } else if (Settings.canDrawOverlays(this)&&startService) {
            ButtonService.context = getApplicationContext();
            startService(new Intent(MainActivity.this, ButtonService.class));
        } else if(startService) {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent intent= new Intent(MainActivity.this,SettingsActivity2.class);
            startActivity(intent);
        } else if (id == R.id.fav) {
            Intent intent= new Intent(MainActivity.this,Favourite.class);
            intent.putStringArrayListExtra("RECORD",recordinglist);
            startActivity(intent);
        } else if (id == R.id.updatecontact) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {

                //Toast.makeText(MainActivity.this, "Contact Database Updated.", Toast.LENGTH_LONG).show();

            }

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Best Call recorder app download now.https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }else if (id == R.id.rate_us) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }else if(id==R.id.recording_issue){
            Intent intent= new Intent(MainActivity.this,Recording_issue.class);
            startActivity(intent);
        }
        // support
//        else if (id == R.id.action_call) {
//            AppUtils.makePhoneCall(mActivity, Constant.CALL_NUMBER);
//        } else if (id == R.id.action_message) {
//            AppUtils.sendSMS(mActivity, Constant.CALL_NUMBER, Constant.SMS_TEXT);
         else if (id == R.id.tutorial) {
            startActivity(new Intent(MainActivity.this,TutorialActivity.class));
        }
        else if (id == R.id.action_email) {
            AppUtils.sendEmail(mActivity, Constant.EMAIL_ADDRESS, Constant.EMAIL_SUBJECT, Constant.EMAIL_BODY);
        }

        // others
        else if (id == R.id.action_rate_app) {
            AppUtils.rateThisApp(mActivity); // this feature will only work after publish the app
        } else if (id == R.id.action_exit) {
            AppUtils.appClosePrompt(mActivity);
        }





        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static  void setQueylistener(querySearch quey){
        queylistener=quey;
    }
    public interface querySearch{
        public void Search_name(String name1);
    }
    public static  void setQueylistener2(querySearch2 quey1){
        queylistener2=quey1;
    }
    public interface querySearch2{
        public void Search_name2(String name1);
    }
    public static  void setQueylistener3(querySearch3 quey3){
        queylistener3=quey3;
    }
    public interface querySearch3{
        public void Search_name3(String name1);
    }


    private  boolean checkAndRequestPermissions() {

        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.clear();
        int recordaudio=ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);//
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);//
        int call= ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);//
        int read_phonestate= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);//
        int Capture_audio_output= ContextCompat.checkSelfPermission(this, Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        int process_outgoing_call= ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);//
        int modify_audio_setting= ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS);//
        int read_contacts= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);//

        if (read_contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (modify_audio_setting != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        }
        if (process_outgoing_call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }

        if (read_phonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (recordaudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (Capture_audio_output!=PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0){

                } else {
                    Toast.makeText(this, "Please Allow All Permission To Continue..", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                   //  phoneContacts=ContactProvider.getContacts(getApplicationContext());
                  //  storeToDatabase(phoneContacts);
        }
    }

    void startAnim(){
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        avi.smoothToShow();
    }

    private void admobbanner(){

        if (Internetconnection.checkConnection(this)) {

            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdMobAdView.loadAd(adRequest);
            final InterstitialAd mInterstitial = new InterstitialAd(this);
            mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
            mInterstitial.loadAd(new AdRequest.Builder().build());
            mInterstitial.setAdListener(new AdListener() {
                @Override
                        public void onAdLoaded() {
                            // TODO Auto-generated method stub
                            super.onAdLoaded();
                            if (mInterstitial.isLoaded()&&shouldLoad) {
                                mInterstitial.show();
                                shouldLoad = false;
                            }
                        }

                @Override
                public void onAdClosed() {
                    mInterstitial.loadAd(new AdRequest.Builder().build());
                    super.onAdClosed();
                }
            });

        } else {
            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            mAdMobAdView.setVisibility(View.GONE);
        }

    }

    public static void setrefreshlistener(refreshstener quey3) {
        refreshlistenerobj = quey3;
    }

    public interface refreshstener {
        public void refresh(boolean b);
    }

    public boolean setSearchQuery() {
        if (!(mSearchView.getQuery().toString().trim().equalsIgnoreCase(""))) {
            if (queylistener != null) {
                queylistener.Search_name(mSearchView.getQuery() + "");
            }
            if (queylistener2 != null) {
                queylistener2.Search_name2(mSearchView.getQuery() + "");
            }
            if (queylistener3 != null) {
                queylistener3.Search_name3(mSearchView.getQuery() + "");
            }
            return true;
        }
        return false;
    }



    @Override
    public void onResume() {
        super.onResume();
        Constant.sIS_FROM_ANOTHER_ACTIVITY = false;
        Constant.sFROM_MAIN_TO_ACTIVITY = false;


    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        shouldLoad = true;
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 10000);
    }

    @Override
    protected void onDestroy() {
        try {
            stopTimer();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
