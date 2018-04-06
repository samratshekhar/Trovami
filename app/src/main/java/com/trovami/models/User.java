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
 * Created by samrat on 12/03/18.
 */

public class User implements Parcelable {
    public static final String TAG = "UserModel";
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String gender;
    public String photoUrl;
    public LatLong latLong;
    public List<String> follower;
    public List<String> following;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public static void setUserById(User user, String uid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("users").child(uid);
        ref.setValue(user);
        return;
    }

    public static void getUserById(String uid, ValueEventListener listener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("users");
        Query phoneQuery = ref.orderByChild(RDBSchema.Users.UID).equalTo(uid);
        phoneQuery.addListenerForSingleValueEvent(listener);
        return;
    }

    public static void getUsers(ValueEventListener listener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("users");
        Query phoneQuery = ref.orderByChild(RDBSchema.Users.UID);
        phoneQuery.addListenerForSingleValueEvent(listener);
        return;
    }

    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        gender = in.readString();
        photoUrl = in.readString();
        latLong = (LatLong) in.readValue(LatLong.class.getClassLoader());
        if (in.readByte() == 0x01) {
            follower = new ArrayList<String>();
            in.readList(follower, String.class.getClassLoader());
        } else {
            follower = null;
        }
        if (in.readByte() == 0x01) {
            following = new ArrayList<String>();
            in.readList(following, String.class.getClassLoader());
        } else {
            following = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(photoUrl);
        dest.writeValue(latLong);
        if (follower == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(follower);
        }
        if (following == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(following);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
