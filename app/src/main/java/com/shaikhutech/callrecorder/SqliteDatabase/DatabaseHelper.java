package com.shaikhutech.callrecorder.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import com.shaikhutech.callrecorder.pojo_classes.Contacts;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_CONTACTS = "contacts";
    //table coloumn fields
    private static final String KEY_ID = "id";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_FAV = "fav";
    private static final String KEY_RECORDING_STATE = "state";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PH_NO + " TEXT, " + KEY_FAV + " INTEGER , "
                + KEY_RECORDING_STATE + " INTEGER" + ")";
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
        values.put(KEY_FAV, contact.getFav());
        values.put(KEY_RECORDING_STATE, contact.getState());
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public Contacts getContact(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID, KEY_PH_NO, KEY_FAV, KEY_RECORDING_STATE}, KEY_PH_NO + "=?",
                new String[]{number}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Contacts contact = new Contacts(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));
        // return contact
        cursor.close();
        return contact;
    }

    public Contacts isContact(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contacts contact = new Contacts();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + KEY_PH_NO + " = '" + number + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNumber(cursor.getString(1));
                contact.setFav(Integer.parseInt(cursor.getString(2)));
                contact.setState(Integer.parseInt(cursor.getString(3)));
                // Adding contact to list
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        return contact;
    }

    // Getting All Contacts
    public ArrayList<Contacts> getAllFavouriteContacts() {
        ArrayList<Contacts> contactList = new ArrayList<Contacts>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + KEY_FAV + "='1'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        contactList.clear();
        if (cursor.moveToFirst()) {
            do {
                Contacts contact = new Contacts();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNumber(cursor.getString(1));
                contact.setFav(Integer.parseInt(cursor.getString(2)));
                contact.setState(Integer.parseInt(cursor.getString(3)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        return contactList;
    }

    public ArrayList<Contacts> allContacts() {
        ArrayList<Contacts> contactList = new ArrayList<Contacts>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contacts contact = new Contacts();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNumber(cursor.getString(1));
                contact.setFav(Integer.parseInt(cursor.getString(2)));
                contact.setState(Integer.parseInt(cursor.getString(3)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        return contactList;
    }

    // Updating single contact
    public int updateContact(Contacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PH_NO, contact.getNumber());
        values.put(KEY_FAV, contact.getFav());
        values.put(KEY_RECORDING_STATE, contact.getState());
        // updating row
        Log.d("id", String.valueOf(contact.getNumber()));
        int ids = db.update(TABLE_CONTACTS, values, KEY_PH_NO + " = ?", new String[]{String.valueOf(contact.getNumber())});
        db.close();
        return ids;
    }

    // Deleting single contact
    public void deleteContact(Contacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}
