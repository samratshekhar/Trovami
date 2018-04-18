package com.trovami.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.trovami.R;
import com.trovami.models.NotificationReq;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TULIKA on 18-Apr-18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private static final String TAG = "NotificationAdapter";
    private static final int SentReq = 1;
    private static final int ReceivedReq = 2;

    private List<NotificationReq> mSentReq = new ArrayList<>();
    private List<NotificationReq> mReceivedReq = new ArrayList<>();
    private Context mContext;

    public NotificationAdapter(Context context, List<User> NotificationReq, List<NotificationReq>) {
        this.mSentReq = mSentReq;
        this.mReceivedReq = mReceivedReq;
        this.mContext = context;
    }

    @NonNull
    @Override
        public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view);
        if(viewType == SentReq){
            return new SentReqViewHolder(view);
        }

        if(viewType == ReceivedReq){
            return new ReceivedReqViewHolder(view);
        }

        return null;
    }

   /* public com.trovami.adapters.NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent
                .getContext()).inflate(viewType, parent, false);
        com.trovami.adapters.NotificationAdapter.ViewHolder holder = new com.trovami.adapters.NotificationAdapter.ViewHolder(view);
        return holder;
    }*/

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        NotificationReq notificationReq = mUnfolllowedUsers.get(position);


        holder.uid.setText(user.uid);
        holder.name.setText(user.name);
        holder.email.setText(user.email);
        holder.phone.setText(user.phone);
        holder.gender.setText(user.gender);
        Glide.with(mContext)
                .load(user.photoUrl)
                .into(holder.photo);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on" + mUnfolllowedUsers.get(position));
                Toast.makeText(mContext, (CharSequence) mUnfolllowedUsers.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSentReq.size() + mReceivedReq.size();
    }

    @Override
    public int getItemViewType(int position){
        if(position < mSentReq.size()){
            return SentReq;
        }

        if(position - mSentReq.size() < mReceivedReq.size()){
            return ReceivedReq;
        }

        return -1;
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder
    {
        public GenericViewHolder(View itemView) {
            super(itemView);
        }

        public abstract  void setDataOnView(int position);
    }

    public class SentReqViewHolder extends GenericViewHolder{

        TextView sent_from;
        TextView sent_to;
        TextView sent_status;
        TextView sent_name;
        ImageView sent_photo;
        RelativeLayout parentLayout;

        public SentReqViewHolder(View itemView) {
            super(itemView);
            sent_photo = itemView.findViewById(R.id.user_Image);
            sent_name = itemView.findViewById(R.id.user_Name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

        @Override
        public void setDataOnView(int position) {

        }
    }
    public class ReceivedReqViewHolder extends GenericViewHolder{

        TextView received_from;
        TextView received_to;
        TextView received_status;
        TextView received_name;
        ImageView received_photo;
        RelativeLayout parentLayout;

        public ReceivedReqViewHolder(View itemView) {
            super(itemView);
            received_photo = itemView.findViewById(R.id.user_Image);
            received_name = itemView.findViewById(R.id.user_Name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

        @Override
        public void setDataOnView(int position) {

        }
    }
}

