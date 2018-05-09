package com.trovami.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.databinding.ActivityMapBinding;
import com.trovami.models.LatLong;
import com.trovami.models.User;
import com.trovami.utils.Utils;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    private static final String TAG = "MapActivity";
    private static final int REQ_PERMISSION = 123;


    private ActivityMapBinding mBinding;
    private MapView mMapView;
    private GoogleMap map;
    private ProgressDialog mDialog;

    private LatLng mOwnLocation;
    private MapFragmentListener mListener;
    private User mUser;
    private User mCurrentUSer;

    private ValueEventListener mUserListener;
    private ValueEventListener mCurrentUserListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI(savedInstanceState);
        setupData();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mUserListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference().child("users").child(mUser.uid).child("latLong")
                    .removeEventListener(mUserListener);
        }
        if (mCurrentUserListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference().child("users").child(mCurrentUSer.uid).child("latLong")
                    .removeEventListener(mCurrentUserListener);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setupMap(googleMap);
//        getOwnLocation();
        startTrackingUsers();
    }

    private void setupUI(Bundle savedInstanceState) {
        setContentView(R.layout.activity_map);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        mMapView = mBinding.mapView;
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Logging in...");
        mDialog.setCancelable(false);
    }

    private void setupData() {
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra("user");
        mCurrentUSer = intent.getParcelableExtra("currentUser");
        mBinding.mapTitleTextView.setText("Tracking " + mUser.name);
        if (mUser.latLong != null && mUser.latLong.timeStamp != null) {
            mBinding.mapSubtitleTextView.setText("Last seen @ " + Utils.formatDateTime(mUser.latLong.timeStamp));
        }
        mBinding.mapTextContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomMap();
            }
        });
    }

    private void setupMap(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);
    }

    private void startTrackingUsers() {
        if (mUser == null && mCurrentUSer == null) {
            Utils.safeToast(getBaseContext(), "No data to track!");
            return;
        }
        mDialog.show();
        if (mUser != null) {
            setupUserLocationListener();
        }
        if (mCurrentUSer != null) {
            setupCurrentUserLocationListener();
        }
    }

    private void setupUserLocationListener() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.uid).child("latLong");
        mUserListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LatLong latLong = dataSnapshot.getValue(LatLong.class);
                mDialog.dismiss();
                if (latLong == null) {
                    Utils.safeToast(getBaseContext(), mUser.name + "'s location data is unavailable!" );
                    return;
                }
                mUser.latLong = latLong;
                clearMap();
                dropMarker(mUser);
                dropMarker(mCurrentUSer);
                zoomMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupCurrentUserLocationListener() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUSer.uid).child("latLong");
        mCurrentUserListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LatLong latLong = dataSnapshot.getValue(LatLong.class);
                mDialog.dismiss();
                if (latLong == null) {
                    Utils.safeToast(getBaseContext(), "Your location data is unavailable!" );
                    return;
                }
                mCurrentUSer.latLong = latLong;
                clearMap();
                dropMarker(mUser);
                dropMarker(mCurrentUSer);
                zoomMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void clearMap() {
        map.clear();
    }

    private void dropMarker(final User user) {
        if (user.latLong != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(user.photoUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_profile_placeholder))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(new LatLng(user.latLong.lat, user.latLong.lon))
                                    .title(user.name)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(resource))));
                            marker.setTag(0);
                        }
                    });
        }
    }

    private void zoomMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mUser.latLong != null) {
            builder.include(new LatLng(mUser.latLong.lat, mUser.latLong.lon));
        }
        if (mCurrentUSer.latLong != null){
            builder.include(new LatLng(mCurrentUSer.latLong.lat, mCurrentUSer.latLong.lon));
        }
        LatLngBounds bounds = builder.build();
        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private void getOwnLocation() {
        if (checkPermission()) {
            map.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                mOwnLocation = new LatLng(latitude, longitude);
            }
        } else {
            askPermission();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();
        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getOwnLocation();
                } else {
                    // Permission denied
                }
                break;
            }
        }
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface MapFragmentListener {
        void onFragmentInteraction(Uri uri);
    }
    }
