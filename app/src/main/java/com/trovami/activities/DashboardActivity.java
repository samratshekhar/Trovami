package com.trovami.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.databinding.ActivityDashboardBinding;
import com.trovami.fragments.HomeFragment;
import com.trovami.fragments.NotificationFragment;
import com.trovami.fragments.UserFragment;
import com.trovami.models.User;
import com.trovami.services.LocationFetchService;
import com.trovami.utils.Utils;

import java.util.Iterator;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DashboardActivity";
    private ActivityDashboardBinding mBinding;
    private FirebaseAuth mAuth;
    private User mCurrentUser;

    private HomeFragment mHomeFragment;
    private NotificationFragment mNotificationFragment;
    private UserFragment mUserFragment;

    private boolean mDoubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupUI();
        setupLocationService();
        setupFirebaseAuth();
        fetchCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        setSupportActionBar(mBinding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mBinding.drawerLayout,
                mBinding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);
        if(mHomeFragment == null) {
            mHomeFragment = HomeFragment.newInstance();
        }
        setFragment(mHomeFragment);
    }

    private void setupLocationService() {
        if (!Utils.isServiceRunning(this, LocationFetchService.class)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            Intent intent = new Intent(this, LocationFetchService.class);
            startService(intent);
        } else {
            Log.d(TAG, "Service up");
        }
    }

    private void fetchCurrentUser() {
        if (mCurrentUser != null) {
            updateUI(mCurrentUser);
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    if(iterator.hasNext()) {
                        // user found, fetch followers and following
                        DataSnapshot singleSnapshot = iterator.next();
                        mCurrentUser = singleSnapshot.getValue(User.class);
                        updateUI(mCurrentUser);
                    } else {
                        createFirebaseUser();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO: handle error
                }
            };
            User.getUserById(currentUser.getUid(), listener);
        }
    }

    private void createFirebaseUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        User user = new User();
        user.email = currentUser.getEmail();
        user.name = currentUser.getDisplayName();
        user.photoUrl = currentUser.getPhotoUrl().toString();
        user.uid = currentUser.getUid();
        User.setUserById(user, currentUser.getUid());
        updateUI(user);
    }

    private void updateUI(final User user) {
        final Activity activity = this;
        // TODO: update porfile pic
        View headerView =  mBinding.navView.getHeaderView(0);
        ImageView profileImageView = headerView.findViewById(R.id.nav_image_view);
        TextView nameTextView = headerView.findViewById(R.id.nav_title_text_view);
        TextView emailTextView = headerView.findViewById(R.id.nav_subtitle_text_view);

        nameTextView.setText(user.name);
        emailTextView.setText(user.email);
        Glide.with(getBaseContext())
                .load(user.photoUrl)
                .into(profileImageView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("isUpdate", true);
                startActivity(intent);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = mBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mDoubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.mDoubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDoubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    private void logout() {
        final Activity activity = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Sign out from Trovami?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent logoutIntent = new Intent(activity, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", null);
        alertDialogBuilder.create().show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if(mHomeFragment == null) {
                mHomeFragment = HomeFragment.newInstance();
            }
            fragment = mHomeFragment;
        } else if (id == R.id.nav_notifications) {
            if(mNotificationFragment == null) {
                mNotificationFragment = NotificationFragment.newInstance();
            }
            fragment = mNotificationFragment;
        } else if (id == R.id.nav_add_user) {
            if (mUserFragment == null) {
                mUserFragment = UserFragment.newInstance();
            }
            fragment = mUserFragment;
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            logout();
        }
        setFragment(fragment);
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment fragment) {


        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
