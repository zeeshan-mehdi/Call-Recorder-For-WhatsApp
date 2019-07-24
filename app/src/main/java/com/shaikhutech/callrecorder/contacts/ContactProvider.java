package com.shaikhutech.callrecorder.contacts;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shaikhutech.callrecorder.Constant;
import com.shaikhutech.callrecorder.Main2Activity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import com.shaikhutech.callrecorder.BuildConfig;
import com.shaikhutech.callrecorder.MainActivity;
import com.shaikhutech.callrecorder.R;
import com.shaikhutech.callrecorder.SqliteDatabase.ContactsDatabase;
import com.shaikhutech.callrecorder.SqliteDatabase.DatabaseHelper;
import com.shaikhutech.callrecorder.pojo_classes.Contacts;
import com.shaikhutech.callrecorder.utils.StringUtils;

public class ContactProvider {
    private static final String TAG = "ContactProvider";
    static refresh itemrefresh;
    static deleterefresh itemdelete;

//    private static final int REQUEST_CODE_SIGN_IN = 0;
//    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
//    private static final int REQUEST_CODE_CREATOR = 2;
//
//    private GoogleSignInClient mGoogleSignInClient;
//    private DriveClient mDriveClient;
//    private DriveResourceClient mDriveResourceClient;
//    private Bitmap mBitmapToSave;


    public static void deletelistener(deleterefresh list) {
        itemdelete = list;
    }

    public static void setItemrefresh(refresh listener) {
        itemrefresh = listener;
    }

    public static ArrayList<Contacts> getContacts(final Context ctx) {
        ArrayList<Contacts> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
//                                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

//                            Bitmap photo = null;
//                            if (inputStream != null) {
////                                photo = BitmapFactory.decodeStream(inputStream);
//                            }
                    while (cursorInfo.moveToNext()) {
                        Contacts info = new Contacts();
                        info.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        info.setNumber(cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                                info.setPhoto(photo);
                        info.setPhotoUri(pURI.toString());
                        list.add(info);
                    }
                    cursorInfo.close();
                }
            }
        }
        cursor.close();
        return list;
    }

    public static String getCurrentTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public static String getRelativeTime(long time) {
        long d = (System.currentTimeMillis() / 1000) - time;
        String remainingTime = "";
        if (d < 60) {
            //seconds
            remainingTime = ((((d % 31536000) % 86400) % 3600) % 60) + "s";
        } else if (d > 60 && d < 3600) {
            //in minutes
            remainingTime = Math.round((((d % 31536000) % 86400) % 3600) / 60) + "m";
        } else if (d > 3600 && d < 86400) {
            //in hours
            remainingTime = Math.round(((d % 31536000) % 86400) / 3600) + "h";
        } else if (d > 86400 && d < 31536000) {
            //in days
            remainingTime = Math.round((d % 31536000) / 86400) + "d";
        } else {
            //in years
            remainingTime = Math.round(d / 31536000) + "y";
        }
        return remainingTime;
    }

    public static long getDaileyTime(long time) {
        long d = (System.currentTimeMillis() / 1000) - time;
        long returntime;
        if (d <= 86400) {
            //today
            returntime = 1;
        } else if (d > 86400 && d < 172800) {
            //yesterday
            returntime = 2;
        } else {
            //
            returntime = time * 1000; //in milisecondd
        }
        return returntime;
    }

    public static ArrayList<Contacts> getCallList(Context ctx, ArrayList<String> recordingList, String type) {
        ArrayList<Contacts> recordedContacts = new ArrayList<>();
        try {


            ArrayList<Contacts> allContactList = new ArrayList<>();
            ContactsDatabase database = new ContactsDatabase(ctx);
            allContactList = database.AllContacts();
            Log.d(TAG, "getCallList: AllContacts Size: " + allContactList.size());
            Log.d(TAG, "getCallList: AllContacts: " + allContactList.toString());

            boolean hascontact = false;
            if (type.equals("IN")) {
                //incoming list
                recordedContacts.clear();
                for (String filename : recordingList) {
                    Log.d(TAG, "getCallList: filename: " + filename);
                    String recordedfilearray[] = filename.split("__");      //recorded file_array
                    try {
                        // String s = recordedfilearray[2];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        continue;
                    } catch (Exception e) {
                        continue;
                    }
                    if (recordedfilearray[2].equals("IN")) {
                        //incoming
                        for (Contacts people : allContactList) {
                            if (StringUtils.prepareContacts(ctx, people.getNumber()).equalsIgnoreCase(recordedfilearray[0])) {
                                long timestamp = new Long(recordedfilearray[1]).longValue();
                                String relative_time = ContactProvider.getRelativeTime(timestamp);
                                Contacts contacts = new Contacts();
                                contacts.setName(people.getName());
                                contacts.setNumber(people.getNumber());
                                contacts.setTime(relative_time);
                                contacts.setPhoto(people.getPhoto());
                                contacts.setPhotoUri(people.getPhotoUri());
                                if (getDaileyTime(timestamp) == 1) {
                                    //today
                                    contacts.setView(1);
                                    contacts.setDate(getDate(timestamp));
                                    contacts.setTimestamp(timestamp);
                                    recordedContacts.add(contacts);
                                } else if (getDaileyTime(timestamp) == 2) {
                                    //yesterday
                                    contacts.setView(2);
                                    contacts.setDate(getDate(timestamp));
                                    contacts.setTimestamp(timestamp);
                                    recordedContacts.add(contacts);
                                } else {
                                    //provide date
                                    contacts.setView(3);
                                    contacts.setDate(getDate(timestamp));
                                    contacts.setTimestamp(timestamp);
                                    recordedContacts.add(contacts);
                                }
                                hascontact = true;
                                break;
                            }
                        }
                        if (!hascontact) {
                            //no contact show them
                            long timestamp = new Long(recordedfilearray[1]).longValue();
                            String relative_time = ContactProvider.getRelativeTime(timestamp);
                            Contacts nocontact = new Contacts();
                            nocontact.setNumber(recordedfilearray[0]);
                            nocontact.setTime(relative_time);
                            nocontact.setDate(getDate(timestamp));
                            if (getDaileyTime(timestamp) == 1) {
                                //today
                                nocontact.setView(1);
                                nocontact.setTimestamp(timestamp);
                                recordedContacts.add(nocontact);
                            } else if (getDaileyTime(timestamp) == 2) {
                                //yesterday
                                nocontact.setView(2);
                                nocontact.setTimestamp(timestamp);
                                recordedContacts.add(nocontact);
                            } else {
                                //provide date
                                nocontact.setView(3);
                                nocontact.setTimestamp(timestamp);
                                recordedContacts.add(nocontact);
                            }
                        } else {
                            hascontact = false;
                        }
                    }
                }
            } else if (type.equals("OUT")) {
                recordedContacts.clear();
                for (String filename : recordingList) {
                    String recordedfilearray[] = filename.split("__");      //recorded file_array
                    try {
                        String s = recordedfilearray[2];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        continue;
                    } catch (Exception e) {
                        continue;
                    }
                    if (recordedfilearray[2].equals("OUT")) {
                        //incoming
                        for (Contacts people : allContactList) {
                            if (StringUtils.prepareContacts(ctx, people.getNumber()).equalsIgnoreCase(recordedfilearray[0])) {
                                long timestamp = new Long(recordedfilearray[1]).longValue();
                                String relative_time = ContactProvider.getRelativeTime(timestamp);
                                Contacts contacts = new Contacts();
                                contacts.setName(people.getName());
                                contacts.setNumber(people.getNumber());
                                contacts.setTime(relative_time);
                                contacts.setPhoto(people.getPhoto());
                                contacts.setDate(getDate(timestamp));
                                contacts.setPhotoUri(people.getPhotoUri());
                                if (getDaileyTime(timestamp) == 1) {
                                    //today
                                    contacts.setView(1);
                                    contacts.setTimestamp(timestamp);
                                    recordedContacts.add(contacts);
                                } else if (getDaileyTime(timestamp) == 2) {
                                    //yesterday
                                    contacts.setView(2);
                                    contacts.setTimestamp(timestamp);
                                    recordedContacts.add(contacts);
                                } else {
                                    //provide date
                                    contacts.setView(3);
                                    contacts.setTimestamp(timestamp);
                                    recordedContacts.add(contacts);
                                }
                                hascontact = true;
                                break;
                            }
                        }

                        if (!hascontact) {
                            //no contact show them
                            long timestamp = new Long(recordedfilearray[1]).longValue();
                            ContactProvider.getRelativeTime(timestamp);
                            String relative_time = ContactProvider.getRelativeTime(timestamp);
                            Contacts nocontact = new Contacts();
                            nocontact.setNumber(recordedfilearray[0]);
                            nocontact.setTime(relative_time);
                            nocontact.setDate(getDate(timestamp));
                            if (getDaileyTime(timestamp) == 1) {
                                //today
                                nocontact.setView(1);
                                nocontact.setTimestamp(timestamp);
                                recordedContacts.add(nocontact);
                            } else if (getDaileyTime(timestamp) == 2) {
                                //yesterday
                                nocontact.setView(2);
                                nocontact.setTimestamp(timestamp);
                                recordedContacts.add(nocontact);
                            } else {
                                //provide date
                                nocontact.setView(3);
                                nocontact.setTimestamp(timestamp);
                                recordedContacts.add(nocontact);
                            }
                        } else {
                            hascontact = false;
                        }
                    }
                }
            } else {
                recordedContacts.clear();
                for (String filename : recordingList) {
                    String recordedfilearray[] = filename.split("__");      //recorded file_array
                    for (Contacts people : allContactList) {
                        if (StringUtils.prepareContacts(ctx, people.getNumber()).equalsIgnoreCase(recordedfilearray[0])) {
                            long timestamp = 1;
                            try {
                                timestamp = new Long(recordedfilearray[1]).longValue();
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                continue;
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                            String relative_time = ContactProvider.getRelativeTime(timestamp);
                            Contacts contacts = new Contacts();
                            contacts.setName(people.getName());
                            contacts.setNumber(people.getNumber());
                            contacts.setTime(relative_time);
                            contacts.setPhoto(people.getPhoto());
                            contacts.setDate(getDate(timestamp));
                            contacts.setPhotoUri(people.getPhotoUri());
                            if (getDaileyTime(timestamp) == 1) {
                                //today
                                contacts.setView(1);
                                contacts.setTimestamp(timestamp);
                                recordedContacts.add(contacts);
                            } else if (getDaileyTime(timestamp) == 2) {
                                //yesterday
                                contacts.setView(2);
                                contacts.setTimestamp(timestamp);
                                recordedContacts.add(contacts);
                            } else {
                                //provide date
                                contacts.setView(3);
                                contacts.setTimestamp(timestamp);
                                recordedContacts.add(contacts);
                            }
                            hascontact = true;
                            break;
                        }
                    }

                    if (!hascontact) {
                        //no contact show them
                        long timestamp = 1;
                        try {
                            timestamp = new Long(recordedfilearray[1]).longValue();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            continue;
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                        //huge error chanceshere fix itbefore its too late
                        ContactProvider.getRelativeTime(timestamp);
                        String relative_time = ContactProvider.getRelativeTime(timestamp);
                        Contacts nocontact = new Contacts();
                        nocontact.setNumber(recordedfilearray[0]);
                        nocontact.setTime(relative_time);
                        nocontact.setDate(getDate(timestamp));
                        if (getDaileyTime(timestamp) == 1) {
                            //today
                            nocontact.setView(1);
                            nocontact.setTimestamp(timestamp);
                            recordedContacts.add(nocontact);
                        } else if (getDaileyTime(timestamp) == 2) {
                            //yesterday
                            nocontact.setView(2);
                            nocontact.setTimestamp(timestamp);
                            recordedContacts.add(nocontact);
                        } else {
                            //provide date
                            nocontact.setView(3);
                            nocontact.setTimestamp(timestamp);
                            recordedContacts.add(nocontact);
                        }
                    } else {
                        hascontact = false;
                    }
                }
            }
            if (!recordedContacts.isEmpty()) {
                addToDatabase(ctx, recordedContacts); //TODO error lies here
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return recordedContacts;

    }


    public static void sendnotification(Context ctx) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(ctx);
        notifyBuilder.setContentTitle("Call recording in progress...");
        try {
            notifyBuilder.setSmallIcon(R.drawable.recording_ico);
        } catch (Exception e) {

        }
        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notifyBuilder.build());
    }

    public static void openMaterialSheetDialog(LayoutInflater inflater, final int position, final String recording, final String contactNumber) {

        View view = inflater.inflate(R.layout.bottom_menu, null);
        DatabaseHelper db = new DatabaseHelper(view.getContext());
        TextView play = view.findViewById(R.id.play);
        TextView favorite = view.findViewById(R.id.fav);
        TextView delete = view.findViewById(R.id.delete);
        TextView turnoff = view.findViewById(R.id.turn_off);
        TextView upload = view.findViewById(R.id.upload);
        TextView share = view.findViewById(R.id.share);
        final Dialog materialSheet = new Dialog(view.getContext(), R.style.MaterialDialogSheet);
        materialSheet.setContentView(view);
        materialSheet.setCancelable(true);
        materialSheet.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        materialSheet.getWindow().setGravity(Gravity.BOTTOM);
        materialSheet.show();
        if (checkFav(view.getContext(), contactNumber)) {
            //set text remove
            favorite.setText(view.getContext().getString(R.string.add_to_favourites_text));
        } else {
            //set text add
            favorite.setText(view.getContext().getString(R.string.remove_favourites_text));
        }
        if (checkContactStateToRecord(view.getContext(), contactNumber)) {
            turnoff.setText("Turn off Recording");
        } else {
            turnoff.setText("Turn on Recording");
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),position, Toast.LENGTH_SHORT).show();
                Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
                Constant.sFROM_MAIN_TO_ACTIVITY = true;
                playmusic(v.getContext(), getFolderPath(v.getContext()) + "/" + recording, recording);
                materialSheet.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getFolderPath(view.getContext()) + "/" + recording);
                if (file.delete()) {
                    //deleted
                    itemdelete.deleterefreshList(true);
                    itemrefresh.refreshList(true);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    //not deleted
                    itemdelete.deleterefreshList(true);
                    itemrefresh.refreshList(true);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_deletion_failed), Toast.LENGTH_SHORT).show();
                }
                materialSheet.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getFolderPath(view.getContext()) + "/" + recording);
                Uri fileuri = FileProvider.getUriForFile(view.getContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
                Intent sendintent = new Intent(Intent.ACTION_SEND);
                sendintent.putExtra(Intent.EXTRA_STREAM, fileuri);
                sendintent.setType("audio/*");
                view.getContext().startActivity(sendintent);
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFavourite(view.getContext(), contactNumber)) {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
                    itemrefresh.refreshList(true);
                } else {
                    itemrefresh.refreshList(true);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.removed_from_favourites), Toast.LENGTH_SHORT).show();
                }
                materialSheet.dismiss();
            }
        });
        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //turn off recording
                if (checkContactStateToRecord(view.getContext(), contactNumber)) {
                    // recording enabled turn it off
                    if (!togglestate(view.getContext(), contactNumber)) {
                        //off
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_turned_off), Toast.LENGTH_SHORT).show();
                        itemrefresh.refreshList(true);
                    }
                } else {
                    if (togglestate(view.getContext(), contactNumber)) {
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_turned_on), Toast.LENGTH_SHORT).show();
                        itemrefresh.refreshList(true);
                    }
                    //recording disabled turn it on
                }
                materialSheet.dismiss();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Upgrade to pro");
                alert.setMessage("Cloud storage feature is available in pro version only. Do you want to upgrade? ");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.callrecorder.procallrecorder")));
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), "Play store not found.", Toast.LENGTH_SHORT).show();
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

                // This is free version hence commented the code of cloud
//                ISaver mSaver;
//                String ONEDRIVE_APP_ID = "60db8ba3-71aa-4a33-bde2-0ba7ef9d0d79";
//                final String filename = getFolderPath(view.getContext()) + "/" + recording;
//                final File f = new File(getFolderPath(view.getContext()) + "/", recording);
//                mSaver = Saver.createSaver(ONEDRIVE_APP_ID);
//                Uri fileuri = Uri.parse("file://" + f.getAbsolutePath());
////                Uri fileuri = FileProvider.getUriForFile(view.getContext(),BuildConfig.APPLICATION_ID + ".provider", f);
//                mSaver.startSaving((Activity) view.getContext(), recording, Uri.fromFile(f));
            }
        });
    }

    public static void showDialog(Context ctx1, final String recording, final Contacts contact) {
        final Dialog dialog = new Dialog(ctx1);
        dialog.setTitle("Select Menu");
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.bottom_menu);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        TextView play = dialog.findViewById(R.id.play);
        TextView favorite = dialog.findViewById(R.id.fav);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView turnoff = dialog.findViewById(R.id.turn_off);
        TextView upload = dialog.findViewById(R.id.upload);
        TextView share = dialog.findViewById(R.id.share);
        if (checkFav(ctx1, contact.getNumber())) {
            //set text remove
            favorite.setText(ctx1.getString(R.string.add_to_favourites_text));
        } else {
            //set text add
            favorite.setText(ctx1.getString(R.string.remove_favourites_text));
        }
        if (checkContactStateToRecord(ctx1, contact.getNumber())) {
            turnoff.setText("Turn off Recording");
        } else {
            turnoff.setText("Turn on Recording");
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),position, Toast.LENGTH_SHORT).show();
                Constant.sIS_FROM_ANOTHER_ACTIVITY = true;
                Constant.sFROM_MAIN_TO_ACTIVITY = true;
                playmusic(v.getContext(), getFolderPath(v.getContext()) + "/" + recording, recording);
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getFolderPath(view.getContext()) + "/" + recording);
                if (file.delete()) {
                    //deleted
//                    itemdelete.deleterefreshList(true);
                    itemrefresh.refreshList(true);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    //not deleted
//                    itemdelete.deleterefreshList(true);
                    itemrefresh.refreshList(true);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_deletion_failed), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getFolderPath(view.getContext()) + "/" + recording);
                Uri fileuri = FileProvider.getUriForFile(view.getContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
                Intent sendintent = new Intent(Intent.ACTION_SEND);
                sendintent.putExtra(Intent.EXTRA_STREAM, fileuri);
                sendintent.setType("audio/*");
                view.getContext().startActivity(sendintent);
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFavourite(view.getContext(), contact.getNumber())) {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
                    itemrefresh.refreshList(true);
                } else {
                    itemrefresh.refreshList(true);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.removed_from_favourites), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //turn off recording
                if (checkContactStateToRecord(view.getContext(), contact.getNumber())) {
                    // recording enabled turn it off
                    if (!togglestate(view.getContext(), contact.getNumber())) {
                        //off
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_turned_off), Toast.LENGTH_SHORT).show();
                        itemrefresh.refreshList(true);
                    }
                } else {
                    if (togglestate(view.getContext(), contact.getNumber())) {
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.recording_turned_on), Toast.LENGTH_SHORT).show();
                        itemrefresh.refreshList(true);
                    }
                    //recording disabled turn it on
                }
                dialog.dismiss();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Upgrade to pro");
                alert.setMessage("Cloud storage feature is available in pro version only. Do you want to upgrade? ");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.callrecorder.procallrecorder")));
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), "Play store not found.", Toast.LENGTH_SHORT).show();
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

                // This is free version hence commented the code of cloud
//                ISaver mSaver;
//                String ONEDRIVE_APP_ID = "60db8ba3-71aa-4a33-bde2-0ba7ef9d0d79";
//                final String filename = getFolderPath(view.getContext()) + "/" + recording;
//                final File f = new File(getFolderPath(view.getContext()) + "/", recording);
//                mSaver = Saver.createSaver(ONEDRIVE_APP_ID);
//                mSaver.startSaving((Activity) view.getContext(), filename, Uri.fromFile(f));
            }
        });
        dialog.show();
    }

    public static void playmusic(Context ctx, String path, String str) {
//        Intent intent = new Intent();
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        File file = new File(path);
//        Uri fileuri = FileProvider.getUriForFile(ctx,
//                BuildConfig.APPLICATION_ID + ".provider",
//                file);
//        intent.setDataAndType(fileuri, "audio/*");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        try{
//            ctx.startActivity(intent);
//        }catch (Exception e){
//            Toast.makeText(ctx, "Media player not found.", Toast.LENGTH_SHORT).show();
//        }

//        final MediaPlayer mp=new MediaPlayer();
//        try {
//            mp.setDataSource(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mp.prepare();
//            mp.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Intent i = new Intent(ctx, Main2Activity.class);
        i.putExtra("PATH", str);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ctx.startActivity(i);
    }

    //SQL Lite Database
    public static boolean checkFavourite(Context context, String number) {
        DatabaseHelper db = new DatabaseHelper(context);
        String contact_no = StringUtils.prepareContacts(context, number);
        Contacts contacts1 = db.isContact(contact_no);
        if (contacts1.getFav() == 0) {
            contacts1.setFav(1);
            int a = db.updateContact(contacts1);
            return true;
        } else if (contacts1.getFav() == 1) {
            contacts1.setFav(0);
            int a = db.updateContact(contacts1);
            return false;
        } else {
            return false;
        }
    }

    public static boolean checkFav(Context context, String number) {
        DatabaseHelper db = new DatabaseHelper(context);
        String contact_no = StringUtils.prepareContacts(context, number);
        Contacts contacts1 = db.isContact(contact_no);
        if (contacts1.getFav() == 0) {
            return true;
        } else if (contacts1.getFav() == 1) {
            return false;
        } else {
            return true;
        }
    }

    public static void addToDatabase(Context ctx, ArrayList<Contacts> recordedContacts) {
        DatabaseHelper db = new DatabaseHelper(ctx);
        for (Contacts cont : recordedContacts) {
            Contacts s = db.isContact(StringUtils.prepareContacts(ctx, cont.getNumber()));
            if (s.getNumber() != null) {
                //has contaact
            } else {
                Contacts sd = new Contacts();
                sd.setFav(0);
                sd.setState(0);
                sd.setNumber(StringUtils.prepareContacts(ctx, cont.getNumber()));
                db.addContact(sd);
            }
        }
    }

    public static boolean checkContactStateToRecord(Context ctx, String number) {
        DatabaseHelper db = new DatabaseHelper(ctx);
        String contact_no = StringUtils.prepareContacts(ctx, number);
        Contacts newcontacts = db.isContact(contact_no);
        if (newcontacts.getNumber() != null) {
            if (newcontacts.getState() == 0) {
                //recording on
                return true;
            } else if (newcontacts.getState() == 1) {
                return false;
                //dont wanna icorecord
            } else {
                return true;
            }
        }
        return true;
    }

    public static boolean togglestate(Context ctx, String number) {
        DatabaseHelper db = new DatabaseHelper(ctx);
        String contact_no = StringUtils.prepareContacts(ctx, number);
        Contacts s = db.isContact(contact_no);
        if (s.getNumber() != null) {
            //has contanct
            if (s.getState() == 0) {
                s.setState(1);
                db.updateContact(s);
                return false;
            } else if (s.getState() == 1) {
                s.setState(0);
                db.updateContact(s);
                return true;
            }
        }
        return true;
    }

    public interface refresh {
        public void refreshList(boolean var);
    }

    public interface deleterefresh {
        public void deleterefreshList(boolean var);
    }

    public static String getFolderPath(Context context) {
        SharedPreferences directorypreference = context.getSharedPreferences("DIRECTORY", Context.MODE_PRIVATE);
        String s = directorypreference.getString("DIR", Environment.getExternalStorageDirectory().getAbsolutePath() + "/CallRecorder");
        return s;
    }

    private static String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate).toString();
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getRecordingNameByContactAndType(Context ctx, ArrayList<String> recordings, String type, Contacts contacts) {
        String newRecordings = "";
        String number = StringUtils.prepareContacts(ctx, contacts.getNumber());
        if (type.equals("IN")) {
            //incoming list
            for (String filename : recordings) {
                String recordedfilearray[] = filename.split("__"); //recorded file_array
                try {
                    String s = recordedfilearray[2];
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    continue;
                } catch (Exception e) {
                    continue;
                }
                if (recordedfilearray[2].equals("IN")) {
                    long timestamp = Long.valueOf(recordedfilearray[1]);
                    if (recordedfilearray[0].equals(number) && timestamp == contacts.getTimestamp()) {
                        return filename;
                    }
                }
            }
        } else if (type.equals("OUT")) {
            for (String filename : recordings) {
                String recordedfilearray[] = filename.split("__");      //recorded file_array
                try {
                    String s = recordedfilearray[2];
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    continue;
                } catch (Exception e) {
                    continue;
                }
                if (recordedfilearray[2].equals("OUT")) {
                    long timestamp = Long.valueOf(recordedfilearray[1]);
                    if (recordedfilearray[0].equals(number) && timestamp == contacts.getTimestamp()) {
                        return filename;
                    }
                }
            }
        } else {
            for (String filename : recordings) {

                String recordedfilearray[] = filename.split("__");      //recorded file_array
                long timestamp = 1;
                try {
                    timestamp = new Long(recordedfilearray[1]).longValue();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recordedfilearray[0].equals(number) && timestamp == contacts.getTimestamp()) {
                    return filename;
                }
            }
        }
        return newRecordings;
    }

    public static ArrayList<String> showAllRecordedlistfilesInNewToOldOrder(Context ctx) {
        String path = ContactProvider.getFolderPath(ctx);
        File f = new File(path);

        File[] listfiles = f.listFiles();

        if (listfiles != null) {
            Arrays.sort(listfiles, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        ArrayList<String> recordedfiles = new ArrayList<>();
        if (listfiles != null) {
            for (File list : listfiles) {
                recordedfiles.add(list.getName());
            }
        }
        return recordedfiles;
    }

    public static ArrayList<String> showAllRecordedlistfiles(Context ctx) {
        ArrayList<String> recordedfiles = new ArrayList<>();
        String path = ContactProvider.getFolderPath(ctx);
        File file = new File(path);
        if (!file.exists()) {
            //no folder empty data
            file.mkdirs();
        }
        File listfiles[] = file.listFiles();
        if (listfiles != null) {
            for (File list : listfiles) {
                recordedfiles.add(list.getName());
            }
        }
        return recordedfiles;
    }


//    /** Start sign in activity. */
//    private void signIn() {
//        Log.i(TAG, "Start sign in");
//        mGoogleSignInClient = buildGoogleSignInClient();
//        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
//    }
//
//    /** Build a Google SignIn client. */
//    private GoogleSignInClient buildGoogleSignInClient() {
//        GoogleSignInOptions signInOptions =
//                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestScopes(Drive.SCOPE_FILE)
//                        .build();
//        return GoogleSignIn.getClient(this, signInOptions);
//    }
//
//    /** Create a new file and save it to Drive. */
//        private void saveFileToDrive() {
//        // Start by creating a new contents, and setting a callback.
//        Log.i(TAG, "Creating new contents.");
//        final Bitmap image = mBitmapToSave;
//
//        mDriveResourceClient
//                .createContents()
//                .continueWithTask(
//                        new Continuation<DriveContents, Task<Void>>() {
//                            @Override
//                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
//                                return createFileIntentSender(task.getResult(), image);
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Failed to create new contents.", e);
//                            }
//                        });
//    }
//
//    /**
//     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
//     * CreateFileActivityOptions} for user to create a new photo in Drive.
//     */
//    private Task<Void> createFileIntentSender(DriveContents driveContents, Bitmap image) {
//        Log.i(TAG, "New contents created.");
//        // Get an output stream for the contents.
//        OutputStream outputStream = driveContents.getOutputStream();
//        // Write the bitmap data from it.
//        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
//        try {
//            outputStream.write(bitmapStream.toByteArray());
//        } catch (IOException e) {
//            Log.w(TAG, "Unable to write file contents.", e);
//        }
//
//        // Create the initial metadata - MIME type and title.
//        // Note that the user will be able to change the title later.
//        MetadataChangeSet metadataChangeSet =
//                new MetadataChangeSet.Builder()
//                        .setMimeType("image/jpeg")
//                        .setTitle("Android Photo.png")
//                        .build();
//        // Set up options to configure and display the create file activity.
//        CreateFileActivityOptions createFileActivityOptions =
//                new CreateFileActivityOptions.Builder()
//                        .setInitialMetadata(metadataChangeSet)
//                        .setInitialDriveContents(driveContents)
//                        .build();
//
//        return mDriveClient
//                .newCreateFileActivityIntentSender(createFileActivityOptions)
//                .continueWith(
//                        new Continuation<IntentSender, Void>() {
//                            @Override
//                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
//                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
//                                return null;
//                            }
//                        });
//    }
//
//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_CODE_SIGN_IN:
//                Log.i(TAG, "Sign in request code");
//                // Called after user is signed in.
//                if (resultCode == RESULT_OK) {
//                    Log.i(TAG, "Signed in successfully.");
//                    // Use the last signed in account here since it already have a Drive scope.
//                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
//                    // Build a drive resource client.
//                    mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
//                    // Start camera.
//                    startActivityForResult(
//                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
//                }
//                break;
//            case REQUEST_CODE_CAPTURE_IMAGE:
//                Log.i(TAG, "capture image request code");
//                // Called after a photo has been taken.
//                if (resultCode == Activity.RESULT_OK) {
//                    Log.i(TAG, "Image captured successfully.");
//                    // Store the image data as a bitmap for writing later.
//                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
//                    saveFileToDrive();
//                }
//                break;
//            case REQUEST_CODE_CREATOR:
//                Log.i(TAG, "creator request code");
//                // Called after a file is saved to Drive.
//                if (resultCode == RESULT_OK) {
//                    Log.i(TAG, "Image successfully saved.");
//                    mBitmapToSave = null;
//                    // Just start the camera again for another photo.
//                    startActivityForResult(
//                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
//                }
//                break;
//        }
//    }


}
