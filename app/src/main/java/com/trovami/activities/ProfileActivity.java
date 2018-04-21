package com.trovami.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.trovami.R;
import com.trovami.databinding.ActivityProfileBinding;
import com.trovami.models.User;

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
        }
        if (mIsUpdate) {
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
        mUser = intent.getParcelableExtra("id");
        mIsUpdate = intent.getBooleanExtra("isUpdate", false);
    }

    void onUpdatePicClicked(View v) {
        Toast.makeText(getApplicationContext(), "Pic edit", Toast.LENGTH_LONG).show();
    }

    void onUpdateFormClicked(View v) {
        Toast.makeText(getApplicationContext(), "Form edit", Toast.LENGTH_LONG).show();
    }
}
