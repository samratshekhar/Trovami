package com.trovami.adapters;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.models.User;
import com.trovami.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anush on 15-04-2018.
 */

public class HomeItemViewHolder extends ChildViewHolder {
    private HomeItemViewListener mClickListener;
    private TextView txtItemTitle;
    private TextView txtItemSubtitle;
    private CircleImageView cimgPhoto;
    private RelativeLayout layoutListParent;
    private Button btnTrack;

    public HomeItemViewHolder(View itemView, HomeItemViewListener clickLister) {
        super(itemView);
        txtItemTitle = itemView.findViewById(R.id.home_item_title_text_view);
        txtItemSubtitle = itemView.findViewById(R.id.home_item_subtitle_text_view);
        cimgPhoto=itemView.findViewById(R.id.home_item_profile_image_view);
        layoutListParent=itemView.findViewById(R.id.home_item_parent_layout);
        btnTrack=itemView.findViewById(R.id.home_item_track_button);
        mClickListener = clickLister;
    }

    public void bind(
            final Context context,
            final String uid,
            final HashMap<String, User> userMap,
            final boolean isFollower) {
        User existingUser = userMap.get(uid);
        if (existingUser != null) {
            bindUserData(context, existingUser, isFollower);
        } else {
            clearUserData();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    if(iterator.hasNext()) {
                        DataSnapshot singleSnapshot = iterator.next();
                        User user = singleSnapshot.getValue(User.class);
                        userMap.put(user.uid, user);
                        bindUserData(context, user, isFollower);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            User.getUserById(uid, listener);
        }


        layoutListParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClicked(uid);
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onActionClicked(uid);
            }
        });
    }

    private void bindUserData(Context context, User user, Boolean isFollower) {
        txtItemTitle.setText(user.name);
        if (user.latLong != null && user.latLong.timeStamp != null) {
            txtItemSubtitle.setText("Last seen: " + Utils.formatDateTime(user.latLong.timeStamp));
        } else  {
            txtItemSubtitle.setText("Last seen: Unknown");
        }
        Glide.with(context)
                .asBitmap()
                .load(user.photoUrl)
                .into(cimgPhoto);
        if (isFollower) {
            btnTrack.setVisibility(View.GONE);
        } else {
            btnTrack.setVisibility(View.VISIBLE);
        }
    }

    private void clearUserData() {
        txtItemTitle.setText("Loading...");
        txtItemSubtitle.setText(null);

    }

    public interface HomeItemViewListener {
        void onItemClicked(String uid);
        void onActionClicked(String uid);

    }
}
