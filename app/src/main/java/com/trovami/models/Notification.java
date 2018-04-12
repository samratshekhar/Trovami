package com.trovami.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samrat on 08/04/18.
 */
public class Notification implements Parcelable {
    public static final String TAG = "NotificationModel";
    public String uid;
    public List<NotificationReq> from;
    public List<NotificationReq> to;
    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public static void getNotificationsById(String uid, ValueEventListener listener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(RDBSchema.Notification.TABLE_NAME);
        Query phoneQuery = ref.orderByChild(RDBSchema.Notification.UID);
        phoneQuery.addListenerForSingleValueEvent(listener);
        return;
    }

    public static void setNotificationById(User user, String uid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child(RDBSchema.Notification.TABLE_NAME).child(uid);
        ref.setValue(user);
        return;
    }

    protected Notification(Parcel in) {
        uid = in.readString();
        if (in.readByte() == 0x01) {
            from = new ArrayList<NotificationReq>();
            in.readList(from, NotificationReq.class.getClassLoader());
        } else {
            from = null;
        }
        if (in.readByte() == 0x01) {
            to = new ArrayList<NotificationReq>();
            in.readList(to, NotificationReq.class.getClassLoader());
        } else {
            to = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        if (from == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(from);
        }
        if (to == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(to);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}