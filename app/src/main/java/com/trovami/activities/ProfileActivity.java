package com.trovami.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trovami.R;
import com.trovami.databinding.ActivityProfileBinding;
import com.trovami.models.RDBSchema;
import com.trovami.models.User;
import com.trovami.utils.ApplicationState;
import com.trovami.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";


    private ActivityProfileBinding mBinding;
    private User mUser;
    private boolean mIsUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupData();
        setupUI();
    }

    private void setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        if (mUser != null){
            mBinding.nameEditText.setText(mUser.name);
            mBinding.emailEditText.setText(mUser.email);
            mBinding.phoneEditText.setText(mUser.phone);
            Glide.with(this)
                    .asBitmap()
                    .load(mUser.photoUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_profile_placeholder))
                    .into(mBinding.profileImage);
            final Activity activity = this;
            mBinding.callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUser.phone != null && !mUser.phone.isEmpty()){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle("Call " + mUser.name + "?");
                        alertDialogBuilder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callUser();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel", null);
                        alertDialogBuilder.create().show();
                    }

                }
            });
            mBinding.mailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUser.email != null && !mUser.email.isEmpty()){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle("Mail " + mUser.name + "?");
                        alertDialogBuilder.setPositiveButton("Mail", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto",mUser.email, null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "via Trovami");
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel", null);
                        alertDialogBuilder.create().show();
                    }

                }
            });
            mBinding.updateFormButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUpdateFormClicked(v);
                }
            });
            mBinding.updatePicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUpdatePicClicked(v);
                }
            });
        }
        if (!mIsUpdate) {
            mBinding.nameEditText.setFocusable(false);
            mBinding.emailEditText.setFocusable(false);
            mBinding.phoneEditText.setFocusable(false);
            mBinding.updateFormButton.setVisibility(View.GONE);
            mBinding.updatePicButton.setVisibility(View.GONE);
            mBinding.callButton.setVisibility(View.VISIBLE);
            mBinding.mailButton.setVisibility(View.VISIBLE);
        } else {
            mBinding.updateFormButton.setVisibility(View.VISIBLE);
            mBinding.updatePicButton.setVisibility(View.VISIBLE);
            mBinding.callButton.setVisibility(View.GONE);
            mBinding.mailButton.setVisibility(View.GONE);
        }
    }

    private void setupData() {
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra("user");
        mIsUpdate = intent.getBooleanExtra("isUpdate", false);
    }

    void onUpdatePicClicked(View v) {
        Utils.safeToast(getBaseContext(), "Pic edit");
    }

    void onUpdateFormClicked(View v) {
        String name = mBinding.nameEditText.getText().toString().trim();
        String email = mBinding.emailEditText.getText().toString().trim();
        String phone = mBinding.phoneEditText.getText().toString().trim();

        String nameValidation = validateName(name);
        String emailValidation = validateEmail(email);
        String phoneValidation = validatePhone(phone);

        if (nameValidation != null) {
            mBinding.nameEditTextLayout.setError(nameValidation);
        } else {
            mBinding.nameEditTextLayout.setError(null);
        }

        if (emailValidation != null) {
            mBinding.emailEditTextLayout.setError(emailValidation);
        } else {
            mBinding.emailEditTextLayout.setError(null);
        }

        if (phoneValidation != null) {
            mBinding.phoneEditTextLayout.setError(phoneValidation);
        } else {
            mBinding.phoneEditTextLayout.setError(null);
        }

        if (nameValidation == null && emailValidation == null && phoneValidation == null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBinding.phoneEditText.getWindowToken(), 0);
            // update user attributes;
            updateUser(name, email, phone);
        }
    }

    private void updateUser(String name, String email, String phone) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", name);
        updateMap.put("email", email);
        updateMap.put("phone", phone);

        mUser.name = name;
        mUser.email = email;
        mUser.phone = phone;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = database.child(RDBSchema.Users.TABLE_NAME).child(mUser.uid);
        userRef.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    ApplicationState state = (ApplicationState)getApplicationContext();
                    state.setCurrentUser(mUser);
                }
            }
        });
        Utils.safeToast(getBaseContext(), "Updating data...");
    }

    private String validateName(String name) {
        if (name == null || name.isEmpty()) return "Name required";
        return null;
    }

    private String validateEmail(String email) {
        if (email == null || email.isEmpty()) return "Email required";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return  "Email invalid";
        return null;
    }

    private String validatePhone(String phone) {
        if (phone == null || phone.isEmpty()) return "Phone required";
        if (!Patterns.PHONE.matcher(phone).matches()) return  "Phone invalid";
        return null;
    }

    private void callUser() {
        if (!Utils.checkIfCallPermissionGranted(this)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE }, 202);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mUser.phone));
                startActivity(intent);
            } catch (SecurityException e) {
                Log.d(TAG, e.getMessage());
                Utils.safeToast(this, "Call permission not provided");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 202:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callUser();
                } else {
                    Utils.safeToast(getBaseContext(), "Call permission needed! Please retry later.");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
