package com.practice.udacity.popularmovieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable{
    private String id;
    private String name;
    private String site;
    private String key;

    public Trailer(String id, String name, String site, String key){
        this.id = id;
        this.name = name;
        this.site = site;
        this.key = key;
    }
    public Trailer(String key, String name){

        this.key = key;
        this.name = name;
    }

    protected Trailer(Parcel in) {
        name = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
    }
}
