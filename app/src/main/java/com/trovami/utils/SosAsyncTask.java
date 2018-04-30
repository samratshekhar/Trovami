package com.trovami.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by samrat on 28/04/18.
 */

public class SosAsyncTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "SosAsyncTask";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient mClient;
    private SosAsyncListener mListener;

    public SosAsyncTask(SosAsyncListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mClient = new OkHttpClient();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        RequestBody body = RequestBody.create(JSON, uidJson(strings[0]));
        Request request = new Request.Builder()
                .url("https://us-central1-trovami-4a67a.cloudfunctions.net/SosNotification")
                .post(body)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                // success
                return true;
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isSuccessful) {
        super.onPostExecute(isSuccessful);
        mListener.onSosComplete(isSuccessful);
    }

    private String uidJson(String uid) {
        return "{\"uid\": \"" + uid + "\"}";
    }

    public interface SosAsyncListener {
        void onSosComplete(boolean isSuccessful);
    }
}
