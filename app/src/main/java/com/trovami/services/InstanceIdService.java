package com.trovami.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by samrat on 26/04/18.
 */

public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "InstanceIdService";

    public InstanceIdService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: "+ token);
    }
}
