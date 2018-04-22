package com.trovami.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.adapters.UserAdapter;
import com.trovami.models.Notification;
import com.trovami.models.RDBSchema;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UserFragment extends Fragment implements UserAdapter.UserActionListener{
    private static final String TAG = "UserFragment";

    private UserFragmentListener mListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private List<User> mUnfolllowedUsers = new ArrayList<>();
    private List<String> mSentReq = new ArrayList<>();
    private User mCurrentUser;
    private UserAdapter mAdapter;

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
                mUnfolllowedUsers.clear();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    User user = singleSnapshot.getValue(User.class);
                    boolean isUnfollowing = isUnfollowed(user.uid);
                    if(isUnfollowing) {
                        mUnfolllowedUsers.add(user);
                    }
                }
                // TODO: update adapter here;
                mAdapter.notifyDataSetChanged();
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
        boolean isAlreadyFollowing = mCurrentUser.following.containsKey(uid);
        boolean isReqSent = mSentReq.contains(uid);
        boolean isCurrentUser = mCurrentUser.uid.equals(uid);
        if (isAlreadyFollowing || isReqSent || isCurrentUser) return false;
        return true;
    }

    private void generateFollowReq(User user) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // add notification entry for current user (to)
        DatabaseReference senderRef = database.child(RDBSchema.Notification.TABLE_NAME).child(mCurrentUser.uid).child("to");
        senderRef.child(user.uid).setValue(user.uid);

        // add notification entry for end user(from)
        DatabaseReference receiverRef = database.child(RDBSchema.Notification.TABLE_NAME).child(user.uid).child("from");
        receiverRef.child(mCurrentUser.uid).setValue(mCurrentUser.uid);

        Toast.makeText(getContext(), "Request sent.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        mAdapter = new UserAdapter(getContext(), mUnfolllowedUsers, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Override
    public void onActionClicked(final User user) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Send follow request to " + user.name);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                generateFollowReq(user);
            }
        });
        alertDialogBuilder.setNegativeButton("No", null);
        alertDialogBuilder.create().show();
    }

    public interface UserFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
