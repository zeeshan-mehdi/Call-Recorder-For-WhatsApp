package com.shaikhutech.callrecorder.pojo_classes;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

public class Contacts  implements Comparable{
    int id;
    int view;
    String name;
    String number;
    Bitmap photo;
    String photoUri;
    String time;
    int fav;
    int state;
    String date;
    String records;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Contacts(){

    }
    public Contacts(int id, String _phone_number,int fav,int state){
        this.id = id;
        this.number = _phone_number;
        this.fav=fav;
        this.state=state;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {

        return time;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getFav() {
        return fav;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int compareage= (int) ((Contacts)o).getTimestamp();
        /* For Ascending order*/
        return (int) (this.timestamp-compareage);

    }
}
