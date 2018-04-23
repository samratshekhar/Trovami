package com.trovami.adapters;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.trovami.R;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by TULIKA on 18-Apr-18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private static final String TAG = "NotificationAdapter";

    private Context mContext;

    private List<String> mReqList = new ArrayList<>();
    private HashMap<String, User> mUserMap;

    public void setmIsReceivedSelected(boolean mIsReceivedSelected) {
        this.mIsReceivedSelected = mIsReceivedSelected;
    }

    private boolean mIsReceivedSelected = true;

    private NotificationActionListener mListener;

    public NotificationAdapter(
            Context context,
            List<String> reqList,
            HashMap<String, User> userMap,
            NotificationActionListener listener,
            boolean isReceivedSelected) {
        this.mReqList = reqList;
        this.mContext = context;
        this.mUserMap = userMap;
        this.mListener = listener;
        this.mIsReceivedSelected = isReceivedSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notification_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        final String uid = mReqList.get(position);
        bindDataToHolder(holder, uid);
        setClickListener(holder, uid);
    }

    private void bindDataToHolder(final ViewHolder holder, String uid) {
        User user = mUserMap.get(uid);
        if (user != null) {
            holder.setViewHolder(user);
        } else {
            holder.clearViewHolder();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    if(iterator.hasNext()) {
                        DataSnapshot singleSnapshot = iterator.next();
                        User currentUser = singleSnapshot.getValue(User.class);
                        mUserMap.put(currentUser.uid, currentUser);
                        holder.setViewHolder(currentUser);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            User.getUserById(uid, listener);
        }
    }

    private void setClickListener(final ViewHolder holder, final String uid) {
        holder.respondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onActionClicked(uid);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReqList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView profilePic;
        Button respondButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            profilePic = itemView.findViewById(R.id.image_view);
            respondButton = itemView.findViewById(R.id.respond_button);
        }

        public void clearViewHolder() {
            titleTextView.setText(null);
        }

        public void setViewHolder(User user) {
            if (!mIsReceivedSelected) {
                respondButton.setVisibility(View.GONE);
                titleTextView.setText("You requested to follow " + user.name + ".");
            } else {
                respondButton.setVisibility(View.VISIBLE);
                titleTextView.setText(user.name + " requested to follow you.");
            }
            Glide.with(mContext)
                    .load(user.photoUrl)
                    .into(profilePic);

        }
    }

    public interface NotificationActionListener {
        void onActionClicked(String uid);
    }
}

