package com.trovami.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samrat on 08/04/18.
 */

public class NotificationReq implements Parcelable {
    public static final String TAG = "NotificationReqModel";
    public String from;
    public String to;
    public String status;
    public String name;
    public String photoUrl;
    public NotificationReq() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    protected NotificationReq(Parcel in) {
        from = in.readString();
        to = in.readString();
        status = in.readString();
        name = in.readString();
        photoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(status);
        dest.writeString(name);
        dest.writeString(photoUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NotificationReq> CREATOR = new Parcelable.Creator<NotificationReq>() {
        @Override
        public NotificationReq createFromParcel(Parcel in) {
            return new NotificationReq(in);
        }

        @Override
        public NotificationReq[] newArray(int size) {
            return new NotificationReq[size];
        }
    };
}