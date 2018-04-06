package com.trovami.models;

/**
 * Created by samrat on 24/03/18.
 */

public class RDBSchema {
    public class Users {
        public static final String TABLE_NAME = "users";
        public static final String UID = "uid";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String GENDER = "gender";
        public static final String PHOTO_URL = "photoUrl";
        public static final String LATLONG = "latLong";
        public static final String FOLLOWER = "follower";
        public static final String FOLLOWING = "following";
    }

    public class LatLong {
        public static final String TABLE_NAME = "latLong";
        public static final String LAT = "lat";
        public static final String LONG = "long";
        public static final String TIME_STAMP = "timeStamp";
    }
}
