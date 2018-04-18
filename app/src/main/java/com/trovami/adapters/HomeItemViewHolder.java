package com.trovami.adapters;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bumptech.glide.Glide;
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
    private Button btnTrack;

    public HomeItemViewHolder(View itemView) {
        super(itemView);
        txtItemTitle = itemView.findViewById(R.id.tV_ListTitle);
        txtItemSubtitle = itemView.findViewById(R.id.tV_ListSubtitle);
        cimgPhoto=itemView.findViewById(R.id.cImg_ListPhoto);
        layoutListParent=itemView.findViewById(R.id.linear_ItemParent);
        btnTrack=itemView.findViewById(R.id.btn_listbtn);
    }

    public void bind(final Context context, final String uid) {
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
                    Glide.with(context)
                            .asBitmap()
                            .load(user.photoUrl)
                            .into(cimgPhoto);

                } else {
                    txtItemTitle.setText(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        User.getUserById(uid, listener);

        layoutListParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"List item for user " + uid, Toast.LENGTH_SHORT).show();
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Button for user " + uid, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
