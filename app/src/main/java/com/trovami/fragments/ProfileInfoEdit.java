package com.trovami.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.trovami.R;
import com.trovami.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileInfoEdit.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileInfoEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileInfoEdit extends Fragment {

    User user;
    EditText nameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    TextView lastupdateTextView;

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
        View view = inflater.inflate(R.layout.fragment_profile_info_edit, container, false);

        nameEditText = view.findViewById(R.id.eT_name);
        emailEditText=view.findViewById(R.id.eT_email);
        phoneEditText=view.findViewById(R.id.eT_phone);
        lastupdateTextView=view.findViewById(R.id.tV_last_update_time);

        nameEditText.setText(user.name);
        emailEditText.setText(user.email);
        phoneEditText.setText("to do");
        lastupdateTextView.setText("to do");

        return view;
    }
}
