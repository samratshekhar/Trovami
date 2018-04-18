package com.trovami.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.models.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";

    private NotificationFragmentListener mListener;
    private FirebaseAuth mAuth;
    private List<String> mSentReq = new ArrayList<>();
    private List<String> mReceivedReq = new ArrayList<>();
    private ProgressDialog mDialog;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        Log.d(TAG, "newInstance");
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Logging in...");
        mDialog.setCancelable(false);
        mDialog.show();
        setupFirebaseAuth();
        fetchNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View v  = inflater.inflate(R.layout.fragment_notification, container, false);
        return v;
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void fetchNotifications() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if(iterator.hasNext()) {
                    // notifications found, fetch followers and following
                    DataSnapshot singleSnapshot = iterator.next();
                    Notification notification = singleSnapshot.getValue(Notification.class);
                    if (notification.to != null) {
                        mSentReq.clear();
                        mSentReq.addAll(notification.to.keySet());
                    }
                    if (notification.from != null) {
                        mReceivedReq.clear();
                        mReceivedReq.addAll(notification.from.keySet());
                    }
                } else {
                    //TODO: handle no notifications here
                }
                mDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: handle error
                mDialog.dismiss();
            }
        };
        Notification.getNotificationsById(currentUser.getUid(), listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof NotificationFragmentListener) {
            mListener = (NotificationFragmentListener) context;
        } else {
            Log.e(TAG, context.toString()
                    + " must implement NotificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        mListener = null;
    }

    public interface NotificationFragmentListener {
        // TODO: expose listeners
    }
}
