package com.shaikhutech.callrecorder.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import com.shaikhutech.callrecorder.pojo_classes.Contacts;

public class ContactsDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts";
    private static final String TABLE_CONTACTS = "phonecontacts";
    //table coloumn fields
    private static final String KEY_ID = "id";
    private static final String KEY_PH_NO = "phone_number";
    private static  final String KEY_NAME="name";
    private  static final String KEY_PHOTOURI="photo";

    public ContactsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PH_NO + " TEXT, "+KEY_NAME+" TEXT , "
                + KEY_PHOTOURI + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }
    //All Crud Information
    public void addContact(Contacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PH_NO, contact.getNumber());
        values.put(KEY_NAME,contact.getName());
        values.put(KEY_PHOTOURI,contact.getPhotoUri());
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<Contacts> AllContacts() {
        ArrayList<Contacts> contactList = new ArrayList<Contacts>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contacts contact = new Contacts();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNumber(cursor.getString(1));
                contact.setName(cursor.getString(2));
                contact.setPhotoUri(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Contacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PH_NO, contact.getNumber());
        values.put(KEY_NAME,contact.getName());
        values.put(KEY_PHOTOURI,contact.getPhotoUri().toString());
        // updating row
        Log.d("id",String.valueOf(contact.getNumber()));
        return db.update(TABLE_CONTACTS, values, KEY_PH_NO + " = ?",
                new String[] { String.valueOf(contact.getNumber()) });
    }

    public Contacts isContact(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contacts contact = new Contacts();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS+" WHERE "+KEY_PH_NO+" = '"+number+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNumber(cursor.getString(1));
                // Adding contact to list
            } while (cursor.moveToNext());
        }
        // return contact list
        return contact;
    }
}
