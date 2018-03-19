package com.trovami.models;

import android.net.Uri;

/**
 * Created by samrat on 12/03/18.
 */

public class User {

    public String uid;
    public String name;
    public String email;
    public String photoUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
}
