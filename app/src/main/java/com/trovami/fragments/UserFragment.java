package com.trovami.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextWatcher;
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
import java.util.Iterator;
import java.util.List;

public class UserFragment extends Fragment implements UserAdapter.UserActionListener{
    private static final String TAG = "UserFragment";

    private UserFragmentListener mListener;
    private FirebaseAuth mAuth;
    private List<User> mUnfolllowedUsers = new ArrayList<>();
    private List<String> mSentReq = new ArrayList<>();
    private User mCurrentUser;
    private UserAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mSwipeListener;

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
        setupFirebaseAuth();
    }

    private void fetchNotifications() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notification notification = dataSnapshot.getValue(Notification.class);
                mSentReq.clear();
                if (notification != null && notification.to != null) {
                    mSentReq.addAll(notification.to.keySet());
                }
                fetchCurrentUser();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mSwipeRefreshLayout.isRefreshing()){
                    Toast.makeText(getActivity(), "Could not refresh data.", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }

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
                    // user found, fetch already following
                    DataSnapshot singleSnapshot = iterator.next();
                    mCurrentUser = singleSnapshot.getValue(User.class);
                    fetchUnfollowedUsers();
                } else {
                    if (mSwipeRefreshLayout.isRefreshing()){
                        Toast.makeText(getActivity(), "Could not refresh data.", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mSwipeRefreshLayout.isRefreshing()){
                    Toast.makeText(getActivity(), "Could not refresh data.", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        };
        User.getUserById(currentUser.getUid(), listener);
    }

    private void fetchUnfollowedUsers() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUnfolllowedUsers.clear();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if(iterator.hasNext()) {
                    while (iterator.hasNext()) {
                        DataSnapshot singleSnapshot = iterator.next();
                        User user = singleSnapshot.getValue(User.class);
                        boolean isUnfollowing = isUnfollowed(user.uid);
                        if(isUnfollowing) {
                            mUnfolllowedUsers.add(user);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()){
                    Toast.makeText(getActivity(), "Refreshed user list!", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mSwipeRefreshLayout.isRefreshing()){
                    Toast.makeText(getActivity(), "Could not refresh data.", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        User.getUsers(listener);
    }

    private boolean isUnfollowed(String uid) {
        boolean isAlreadyFollowing = false;
        if (mCurrentUser.following != null) {
            isAlreadyFollowing = mCurrentUser.following.containsKey(uid);
        }
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

        //Toast.makeText(getActivity(), "Request sent.",Toast.LENGTH_SHORT).show();
        startRefresh();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        setupListView(v);
        setupSwipeRefresh(v);
        setupSearchView(v);
        return v;
    }

    private void setupListView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        mAdapter = new UserAdapter(getActivity(), mUnfolllowedUsers, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupSwipeRefresh(View v) {
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_layout);
        mSwipeListener  = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNotifications();
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeListener);
        startRefresh();
    }

    private void startRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeListener.onRefresh();
            }
        });
    }

    private void setupSearchView(View v) {
        SearchView searchView = v.findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    void filter(String text){
        if (text != null) {
            List<User> temp = new ArrayList();
            for(User d: mUnfolllowedUsers){
                if(d.name.toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }
            mAdapter.updateList(temp);
        }
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
