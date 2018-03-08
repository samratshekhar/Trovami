package com.trovami.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trovami.R;
import com.trovami.fragments.ProfileInfoRo;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileInfoRo fragment = new ProfileInfoRo();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_profile_info, fragment).commit();
    }
}
