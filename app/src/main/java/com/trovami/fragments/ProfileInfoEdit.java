package com.trovami.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trovami.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileInfoEdit.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileInfoEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileInfoEdit extends Fragment {
    public ProfileInfoEdit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_info_edit, container, false);
    }
}
