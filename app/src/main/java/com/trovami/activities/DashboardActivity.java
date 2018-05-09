package com.trovami.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.trovami.fragments.AboutFragment;
import com.trovami.fragments.HomeFragment;
import com.trovami.fragments.NotificationFragment;
import com.trovami.fragments.UserFragment;
import com.trovami.models.User;
import com.trovami.services.LocationFetchService;
import com.trovami.utils.SosAsyncTask;
import com.trovami.utils.Utils;

import java.util.Iterator;

import okhttp3.internal.Util;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SosAsyncTask.SosAsyncListener{

    private static final String TAG = "DashboardActivity";
    private ActivityDashboardBinding mBinding;
    private FirebaseAuth mAuth;
    private User mCurrentUser;
    private ProgressDialog mDialog;

    private HomeFragment mHomeFragment;
    private NotificationFragment mNotificationFragment;
    private AboutFragment mAboutFragment;
    private UserFragment mUserFragment;

    private boolean mDoubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupUI();
        setupLocationService();
        setupFirebaseAuth();
        setupFcmToken();
        fetchCurrentUser(getIntent().getStringExtra("uid"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mHomeFragment == null) {
            mHomeFragment = HomeFragment.newInstance();
        }
        setFragment(mHomeFragment, "Home");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fetchCurrentUser(intent.getStringExtra("uid"));
    }

    private void handleSosNotification(String sosUid) {
        if (sosUid != null) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    if(iterator.hasNext()) {
                        // user found, fetch followers and following
                        DataSnapshot singleSnapshot = iterator.next();
                        User user = singleSnapshot.getValue(User.class);
                        Intent mapIntent = new Intent(DashboardActivity.this, MapActivity.class);
                        mapIntent.putExtra("user", user);
                        mapIntent.putExtra("currentUser", mCurrentUser);
                        startActivity(mapIntent);
                    } else {
                        Utils.safeToast(DashboardActivity.this, "Unable to fetch user!");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Utils.safeToast(DashboardActivity.this, "Unable to fetch user!");
                }
            };
            User.getUserById(sosUid, listener);
        }
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupFcmToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token =  preferences.getString("fcmToken", null);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        User.setFcmToken(token, currentUser.getUid());
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
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSosCall();
            }
        });
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Sending out Sos call...");
        mDialog.setCancelable(false);
    }

    private void setupLocationService() {
        if (!Utils.checkIfLocationPermissionGranted(this)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, 101);
        } else {
            if (!Utils.isServiceRunning(this, LocationFetchService.class)) {
                Intent intent = new Intent(this, LocationFetchService.class);
                startService(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupLocationService();
                } else {
                    Utils.safeToast(getBaseContext(), "Location permission needed! Please restart app.");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void teardownLocationService() {
        if (Utils.isServiceRunning(this, LocationFetchService.class)) {
            Intent intent = new Intent(this, LocationFetchService.class);
            stopService(intent);
        }
    }

    private void fetchCurrentUser(final String sosUid) {
        if (mCurrentUser != null) {
            updateUI(mCurrentUser);
            handleSosNotification(sosUid);
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
                        handleSosNotification(sosUid);
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
            Utils.safeToast(getBaseContext(), "Please click BACK again to exit");

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
                User.clearFcmToken(mAuth.getCurrentUser().getUid());
                teardownLocationService();
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
        String title = "";
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if(mHomeFragment == null) {
                mHomeFragment = HomeFragment.newInstance();
            }
            fragment = mHomeFragment;
            title = "Home";
        } else if (id == R.id.nav_notifications) {
            if(mNotificationFragment == null) {
                mNotificationFragment = NotificationFragment.newInstance();
            }
            fragment = mNotificationFragment;
            title = "Requests";
        } else if (id == R.id.nav_add_user) {
            if (mUserFragment == null) {
                mUserFragment = UserFragment.newInstance();
            }
            fragment = mUserFragment;
            title = "Add User";
        } else if (id == R.id.nav_about) {
            if(mAboutFragment == null){
                mAboutFragment= AboutFragment.newInstance();
            }
            fragment = mAboutFragment;
            title = "About";
        } else if (id == R.id.nav_logout) {
            logout();
        }
        setFragment(fragment, title);
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment fragment, String title) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit();
            mBinding.toolbar.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSosCall() {
        final Activity activity = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Send out SoS call?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.show();
                SosAsyncTask sos = new SosAsyncTask(DashboardActivity.this);
                sos.execute(mAuth.getCurrentUser().getUid());
            }
        });
        alertDialogBuilder.setNegativeButton("No", null);
        alertDialogBuilder.create().show();
    }

    @Override
    public void onSosComplete(boolean isSuccessful) {
        mDialog.dismiss();
        if (isSuccessful) {
            Utils.safeToast(getBaseContext(), "SoS call sent successfully!");
        } else {
            Utils.safeToast(getBaseContext(), "Error sending SoS call!");
        }
    }
}
