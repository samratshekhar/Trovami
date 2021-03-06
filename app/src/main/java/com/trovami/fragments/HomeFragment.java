package com.trovami.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.activities.MapActivity;
import com.trovami.activities.ProfileActivity;
import com.trovami.adapters.HomeItemViewHolder;
import com.trovami.adapters.HomeRecycleExpandableAdapater;
import com.trovami.models.HomeGroup;
import com.trovami.models.User;
import com.trovami.utils.ApplicationState;
import com.trovami.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by samrat on 27/01/18.
 */

public class HomeFragment extends Fragment implements HomeItemViewHolder.HomeItemViewListener {

    private static final String TAG = "NotificationFragment";

    private HomeFragmentListener mListener;
    private FirebaseAuth mAuth;
    private User mCurrentUser;
    private HomeRecycleExpandableAdapater mHomeRecycleExpandableAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout.OnRefreshListener mSwipeListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private HashMap<String, User> mUserMap = new HashMap<>();
    private List<HomeGroup> mGrouplist = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        Log.d(TAG, "newInstance");
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setupListView(v);
        setupSwipeRefreshLayout(v);
        return v;
    }

    private void setupListView(View v) {
        if (mGrouplist.isEmpty()) {
            mGrouplist.add(new HomeGroup("You're following (0)",new ArrayList<String>()));
            mGrouplist.add(new HomeGroup("Your followers (0)",new ArrayList<String>()));
        }
        mHomeRecycleExpandableAdapter = new HomeRecycleExpandableAdapater(getContext(),mGrouplist, mUserMap, this);
        mHomeRecycleExpandableAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onParentExpanded(int parentPosition) {
                showEmptyText(parentPosition);
            }

            @Override
            public void onParentCollapsed(int parentPosition) {
                showEmptyText(parentPosition);
            }
        });
        mRecyclerView= v.findViewById(R.id.recyclerView_home);
        mRecyclerView.setAdapter(mHomeRecycleExpandableAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showEmptyText(int parentPosition) {
        if (mCurrentUser != null) {
            if (parentPosition == 0) {
                if (mCurrentUser.following == null || mCurrentUser.following.keySet().isEmpty()) {
                    Utils.safeToast(getContext(), "You're not following anybody!");
                }
            } else {
                if (mCurrentUser.follower == null || mCurrentUser.follower.keySet().isEmpty()) {
                    Utils.safeToast(getContext(), "You're not being followed yet!");
                }
            }
        }
    }

    private void setupSwipeRefreshLayout(View v) {
        mSwipeRefreshLayout = v.findViewById(R.id.swipeLayout);
        mSwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchCurrentUser();
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

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
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
                    if (getActivity() != null) {
                        ApplicationState state = (ApplicationState)getActivity().getApplicationContext();
                        state.setCurrentUser(singleSnapshot.getValue(User.class));
                    }

                    mGrouplist.get(0).getChildList().clear();
                    mGrouplist.get(1).getChildList().clear(); //to avoid duplication when screen refreshed
                    if (mCurrentUser.following != null) {
                        mGrouplist.get(0).setGroupName("You're following (" + mCurrentUser.following.keySet().size() + ")");
                        mGrouplist.get(0).getChildList().addAll(mCurrentUser.following.keySet());
                        mHomeRecycleExpandableAdapter.expandParent(0);
                    } else {
                        mGrouplist.get(0).setGroupName("You're following (0)");
                    }
                    if (mCurrentUser.follower != null) {
                        mGrouplist.get(1).setGroupName("Your followers (" + mCurrentUser.follower.keySet().size() + ")");
                        mGrouplist.get(1).getChildList().addAll(mCurrentUser.follower.keySet());
                    } else {
                        mGrouplist.get(1).setGroupName("Your followers (0)");
                    }
                    mHomeRecycleExpandableAdapter.notifyParentDataSetChanged(true);
                } else {
                    // user not found, create one
                    createFirebaseUser();
                }
                if (mSwipeRefreshLayout.isRefreshing()){
                    Utils.safeToast(getContext(), "Refreshed trackers!");
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mSwipeRefreshLayout.isRefreshing()){
                    Utils.safeToast(getContext(), "Could not refresh data.");
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        User.getUserById(currentUser.getUid(), listener);
    }

    private void createFirebaseUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        User user = new User();
        user.email = currentUser.getEmail();
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            user.name = currentUser.getDisplayName();
        } else {
            user.name = currentUser.getEmail().split("@")[0];
        }
        if (currentUser.getPhotoUrl() != null) {
            user.photoUrl = currentUser.getPhotoUrl().toString();
        }
        user.uid = currentUser.getUid();
        User.setUserById(user, currentUser.getUid());
        if (mSwipeRefreshLayout.isRefreshing()){
            Utils.safeToast(getContext(), "Refreshed trackers!");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof HomeFragmentListener) {
            mListener = (HomeFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HomeFragmentListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClicked(String uid) {
        User user = mUserMap.get(uid);
        if (user != null){
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

    @Override
    public void onActionClicked(String uid) {
        Intent intent = new Intent(getContext(), MapActivity.class);
        if (mUserMap.get(uid) != null) {
            intent.putExtra("user", mUserMap.get(uid));
            intent.putExtra("currentUser", mCurrentUser);
            startActivity(intent);
        } else {
            Utils.safeToast(getContext(), "Unable to fetch user data!");
        }

    }
}
