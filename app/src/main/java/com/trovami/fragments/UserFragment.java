package com.trovami.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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
import com.trovami.models.NotificationReq;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";

    private UserFragmentListener mListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private List<User> mUnfolllowedUsers;
    private List<NotificationReq> mSentReq = new ArrayList<>();
    private User mCurrentUser;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
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
        fetchCurrentUser();
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
                    mSentReq.addAll(notification.to);
                } else {
                    //TODO: handle no notifications here
                }
                fetchCurrentUser();
                mDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: handle no notifications here
                mDialog.dismiss();
            }
        };
        Notification.getNotificationsById(currentUser.getUid(), listener);
    }

    private void fetchCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if(iterator.hasNext()) {
                    // user found, fetch followers and following
                    DataSnapshot singleSnapshot = iterator.next();
                    mCurrentUser = singleSnapshot.getValue(User.class);
                    fetchUnfollowedUsers();
                } else {
                    // TODO: handle user req here
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: handle user req here
                mDialog.dismiss();
            }
        };
        User.getUserById(currentUser.getUid(), listener);
    }

    private void fetchUnfollowedUsers() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUnfolllowedUsers = new ArrayList<>();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    User user = singleSnapshot.getValue(User.class);
                    if(!mCurrentUser.following.contains(user.uid) && mCurrentUser.uid != user.uid) {
                        mUnfolllowedUsers.add(user);
                    }
                }
                // TODO: update adapter here;
                mDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: handle error
                mDialog.dismiss();
            }
        };
        User.getUsers(listener);
    }

    private boolean isUnfollowed(String uid) {
        boolean isReqSent = false;
        Iterator<NotificationReq> iterator = mSentReq.iterator();
        while (iterator.hasNext()) {
            NotificationReq sentReq = iterator.next();
            if(sentReq.to == uid) {
                isReqSent = true;
                break;
            }
        }
        if (
                mCurrentUser.following.contains(uid)
                && mCurrentUser.uid != uid
                && isReqSent

           ) {
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        return v;
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserFragmentListener) {
            mListener = (UserFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface UserFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
