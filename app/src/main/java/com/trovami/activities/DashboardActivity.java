package com.trovami.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.databinding.ActivityDashboardBinding;
import com.trovami.fragments.HomeFragment;
import com.trovami.fragments.MapFragment;
import com.trovami.fragments.NotificationFragment;
import com.trovami.models.Notification;
import com.trovami.models.User;
import com.trovami.services.LocationFetchService;
import com.trovami.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DashboardActivity";
    private ActivityDashboardBinding mBinding;

    private HomeFragment mHomeFragment;
    private NotificationFragment mNotificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupUI();
        if (!Utils.isServiceRunning(this, LocationFetchService.class)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            Intent intent = new Intent(this, LocationFetchService.class);
            startService(intent);
        } else {
            Log.d(TAG, "Service up");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        setSupportActionBar(mBinding.toolbar);

        DrawerLayout drawer = mBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = mBinding.navView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = mBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            if(mHomeFragment == null) {
                mHomeFragment = HomeFragment.newInstance();
            }
            fragment = mHomeFragment;
        } else if (id == R.id.nav_gallery) {
            if(mNotificationFragment == null) {
                mNotificationFragment = NotificationFragment.newInstance();
            }
            fragment = mNotificationFragment;
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent logoutIntent = new Intent(this, MainActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
