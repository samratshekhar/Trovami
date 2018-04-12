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
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.activities.DashboardActivity;
import com.trovami.adapters.HomeExpandableAdapter;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by samrat on 27/01/18.
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "NotificationFragment";

    private HomeFragmentListener mListener;
    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;
    private HomeExpandableAdapter mHomeExpandableAdapter;
    private ExpandableListView mExpandableListView;

    private List<User> mFollowings;
    private List<User> mFollowers;
    private HashMap<String, List<String>> userIdMap = new HashMap<>();

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
        Log.d(TAG, "onCreate");
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Logging in...");
        mDialog.setCancelable(false);
        mDialog.show();
        setupFirebaseAuth();
        fetchCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        List<String> headers = Arrays.asList("Follower","Following");
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mHomeExpandableAdapter = new HomeExpandableAdapter(getContext(), headers , userIdMap);
        mExpandableListView = v.findViewById(R.id.expandable_list_view);
        mExpandableListView.setAdapter(mHomeExpandableAdapter);
        return v;
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void fetchCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final HomeFragment fragment = this;
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if(iterator.hasNext()) {
                    // user found, fetch followers and following
                    DataSnapshot singleSnapshot = iterator.next();
                    User user = singleSnapshot.getValue(User.class);
//                    fragment.fetchFollowLists(user.following, user.follower);
                    userIdMap.put("Follower", user.follower);
                    userIdMap.put("Following", user.following);
                    mHomeExpandableAdapter.notifyDataSetChanged();

                } else {
                    // user not found, create one
                    fragment.createFirebaseUser();
                }
                mDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: handle error
                mDialog.dismiss();
            }
        };
        User.getUserById(currentUser.getUid(), listener);
    }

    private void fetchFollowLists(final List<String> following, final List<String> follower) {
        User.getUsers(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    User user = singleSnapshot.getValue(User.class);
                    if(following.contains(user.uid)) {
                        if (mFollowings == null) {
                            mFollowings = new ArrayList<User>();
                        }
                        mFollowings.add(user);
                    }
                    else  if (follower.contains(user.uid)) {
                        if (mFollowers == null) {
                            mFollowers = new ArrayList<User>();
                        }
                        mFollowers.add(user);
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
        });
    }

    private void createFirebaseUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        User user = new User();
        user.email = currentUser.getEmail();
        user.name = currentUser.getDisplayName();
        user.photoUrl = currentUser.getPhotoUrl().toString();
        user.uid = currentUser.getUid();
        User.setUserById(user, currentUser.getUid());
        mDialog.dismiss();
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
}
