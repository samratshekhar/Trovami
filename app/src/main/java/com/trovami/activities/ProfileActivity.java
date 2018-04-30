package com.trovami.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trovami.R;
import com.trovami.databinding.ActivityProfileBinding;
import com.trovami.models.RDBSchema;
import com.trovami.models.User;
import com.trovami.utils.Utils;

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
                    .into(mBinding.profileImage);
        }
        if (!mIsUpdate) {
            mBinding.nameEditText.setFocusable(false);
            mBinding.emailEditText.setFocusable(false);
            mBinding.phoneEditText.setFocusable(false);
            mBinding.updateFormButton.setVisibility(View.GONE);
            mBinding.updatePicButton.setVisibility(View.GONE);
        } else {
            mBinding.updateFormButton.setVisibility(View.VISIBLE);
            mBinding.updatePicButton.setVisibility(View.VISIBLE);
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
            // update user attributes;
            updateUser(name, email, phone);
        }
    }

    private void updateUser(String name, String email, String phone) {
        mUser.name = name;
        mUser.email = email;
        mUser.phone = phone;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = database.child(RDBSchema.Users.TABLE_NAME).child(mUser.uid);
        userRef.setValue(mUser);
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
}
