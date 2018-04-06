package com.trovami.models;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by samrat on 12/03/18.
 */

public class User {
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

    public static User setUserById(User user, String uid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("users").child(uid);
        ref.setValue(user);
        return null;
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
}
