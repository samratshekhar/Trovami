package com.trovami.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trovami.R;

/**
 * Created by samrat on 27/01/18.
 */

public class MainFragment extends Fragment {
    public MainFragment() {
        // Required empty public constructor

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (View) inflater.inflate(R.layout.fragment_main, container, false);
    }
}

