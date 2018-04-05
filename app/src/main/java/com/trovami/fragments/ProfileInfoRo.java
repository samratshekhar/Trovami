package com.trovami.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trovami.R;
import com.trovami.models.User;

public class ProfileInfoRo extends Fragment {

    User user;
    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;
    TextView lastupdateTextView;

    public ProfileInfoRo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info_ro, container, false);

        nameTextView = view.findViewById(R.id.tV_name);
        emailTextView=view.findViewById(R.id.tV_email);
        phoneTextView=view.findViewById(R.id.tV_phone);
        lastupdateTextView=view.findViewById(R.id.tV_last_update_time);

        nameTextView.setText(user.name);
        emailTextView.setText(user.email);
        phoneTextView.setText("to do");
        lastupdateTextView.setText("to do");

        return view;
    }
}
