package com.trovami.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.trovami.adapters.NotificationAdapter;
import com.trovami.adapters.UserAdapter;
import com.trovami.models.Notification;
import com.trovami.models.RDBSchema;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NotificationFragment extends Fragment implements NotificationAdapter.NotificationActionListener {

    private static final String TAG = "NotificationFragment";

    private NotificationFragmentListener mListener;
    private FirebaseAuth mAuth;
    private List<String> mSentReq = new ArrayList<>();
    private List<String> mReceivedReq = new ArrayList<>();

    private HashMap<String, User> mUserMap = new HashMap<>();
    private List<String> mDisplayList = new ArrayList<>();
    private NotificationAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mSwipeListener;

    private TabLayout mTabLayout;

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
        setupFirebaseAuth();
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void fetchNotifications() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notification notification = dataSnapshot.getValue(Notification.class);
                mSentReq.clear();
                mReceivedReq.clear();
                if (notification != null) {
                    if (notification.to != null) {
                        mSentReq.addAll(notification.to.keySet());
                    }
                    if (notification.from != null) {
                        mReceivedReq.addAll(notification.from.keySet());
                    }
                }
                updateListView(mTabLayout.getSelectedTabPosition() == 0);
                dismissRefresh("Refreshed notifications!");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissRefresh("Could not refresh data.");
            }
        };
        Notification.getNotificationsById(currentUser.getUid(), listener);
    }

    private void acceptRequest(String uid) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // TODO: update following list
        DatabaseReference followingRef = database
                .child(RDBSchema.Users.TABLE_NAME)
                .child(uid)
                .child(RDBSchema.Users.FOLLOWING);
        followingRef.
                child(currentUser.getUid())
                .setValue(currentUser.getUid());
        // TODO: update follower list
        DatabaseReference followerRef = database
                .child(RDBSchema.Users.TABLE_NAME)
                .child(currentUser.getUid())
                .child(RDBSchema.Users.FOLLOWER);
        followerRef
                .child(uid)
                .setValue(uid);
        deleteRequest(uid);
    }

    private void deleteRequest(String uid) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // TODO: remove from notification
        DatabaseReference senderRef = database
                .child(RDBSchema.Notification.TABLE_NAME)
                .child(currentUser.getUid())
                .child(RDBSchema.Notification.FROM);
        senderRef.child(uid).removeValue();

        // TODO: remove to notification
        DatabaseReference receiverRef = database
                .child(RDBSchema.Notification.TABLE_NAME)
                .child(uid)
                .child(RDBSchema.Notification.TO);
        receiverRef.child(currentUser.getUid()).removeValue();
        startRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View v  = inflater.inflate(R.layout.fragment_notification, container, false);
        setupListView(v);
        setupSwipeRefresh(v);
        setupTabLayout(v);
        return v;
    }

    private void setupTabLayout(View v) {
        mTabLayout = v.findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateListView(tab.getPosition() == 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void updateListView(boolean isReceivedSelected) {
        mDisplayList.clear();
        if (isReceivedSelected) {
            mDisplayList.addAll(mReceivedReq);
        } else {
            mDisplayList.addAll(mSentReq);
        }
        mAdapter.setmIsReceivedSelected(isReceivedSelected);
        mAdapter.notifyDataSetChanged();
    }

    private void setupListView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        mAdapter = new NotificationAdapter(getActivity(), mDisplayList, mUserMap, this, true);
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

    private void dismissRefresh(String msg) {
        if (mSwipeRefreshLayout.isRefreshing()){
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
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

    @Override
    public void onActionClicked(String uid) {
        final User user = mUserMap.get(uid);
        if (user != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Respond to follow request by " + user.name);
            alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    acceptRequest(user.uid);
                }
            });
            alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteRequest(user.uid);
                }
            });
            alertDialogBuilder.create().show();
        }
    }

    public interface NotificationFragmentListener {
        // TODO: expose listeners
    }
}
