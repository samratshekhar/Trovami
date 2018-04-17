package com.trovami.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.models.User;

import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anush on 15-04-2018.
 */

public class HomeItemViewHolder extends ChildViewHolder {
    private TextView txtItemTitle;
    private  TextView txtItemSubtitle;
    private CircleImageView cimgPhoto;
    private LinearLayout layoutListParent;

    public HomeItemViewHolder(View itemView) {
        super(itemView);
        txtItemTitle = itemView.findViewById(R.id.tV_ListTitle);
        txtItemSubtitle = itemView.findViewById(R.id.tV_ListSubtitle);
        cimgPhoto=itemView.findViewById(R.id.cImg_ListPhoto);
        layoutListParent=itemView.findViewById(R.id.linear_ItemParent);
    }

    public void bind(String uid) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if(iterator.hasNext()) {
                    // user found, fetch followers and following
                    DataSnapshot singleSnapshot = iterator.next();
                    User user = singleSnapshot.getValue(User.class);
//                    fragment.fetchFollowLists(user.following, user.follower);
                    txtItemTitle.setText(user.name);
                } else {
                    txtItemTitle.setText(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        User.getUserById(uid, listener);
//        txtItemTitle.setText(uid);
    }
}
