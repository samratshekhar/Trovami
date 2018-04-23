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
    private static final int SentReq = 1;
    private static final int ReceivedReq = 2;

    private List<String> mReqList = new ArrayList<>();
    private Context mContext;
    private HashMap<String, User> mUserMap;

    public NotificationAdapter(Context context, List<String> reqList, HashMap<String, User> userMap) {
        this.mReqList = reqList;
        this.mContext = context;
        this.mUserMap = userMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        User user = mUserMap.get(mReqList.get(position));
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
            User.getUserById(mReqList.get(position), listener);
        }
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
            titleTextView.setText(user.name);
            Glide.with(mContext)
                    .load(user.photoUrl)
                    .into(profilePic);

        }
    }
}

