package com.trovami.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samrat on 23/03/18.
 */

public class LatLong implements Parcelable {

    public double lat;
    public double lon;
    public String timeStamp;

    public LatLong() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    protected LatLong(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
        timeStamp = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(timeStamp);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LatLong> CREATOR = new Parcelable.Creator<LatLong>() {
        @Override
        public LatLong createFromParcel(Parcel in) {
            return new LatLong(in);
        }

        @Override
        public LatLong[] newArray(int size) {
            return new LatLong[size];
        }
    };
}
