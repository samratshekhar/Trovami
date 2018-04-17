package com.trovami.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.trovami.adapters.HomeRecycleExpandableAdapater;
import com.trovami.models.HomeGroup;
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
    //private HomeExpandableAdapter mHomeExpandableAdapter;
    private HomeRecycleExpandableAdapater mHomeRecycleExpandableAdapter;
    // private ExpandableListView mExpandableListView;
    private RecyclerView mRecyclerView;

    private HashMap<String, User> userMap = new HashMap<>();
    private HashMap<String, List<String>> userIdMap = new HashMap<>();
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

//        mHomeExpandableAdapter = new HomeExpandableAdapter(getContext(), headers , userIdMap, userMap);
//        mExpandableListView = v.findViewById(R.id.expandable_list_view);
//        mExpandableListView.setAdapter(mHomeExpandableAdapter);


        mGrouplist.add(new HomeGroup("Following",new ArrayList<String>()));
        mGrouplist.add(new HomeGroup("Followers",new ArrayList<String>()));


        mHomeRecycleExpandableAdapter = new HomeRecycleExpandableAdapater(getContext(),mGrouplist);
        mRecyclerView= v.findViewById(R.id.recyclerView_home);
        mRecyclerView.setAdapter(mHomeRecycleExpandableAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


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
                    if (user.following != null) {
                        mGrouplist.get(0).getChildList().addAll(user.following);
                    }
                    if (user.follower != null) {
                        mGrouplist.get(1).getChildList().addAll(user.follower);
                    }
                    mHomeRecycleExpandableAdapter.notifyParentDataSetChanged(true);
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
