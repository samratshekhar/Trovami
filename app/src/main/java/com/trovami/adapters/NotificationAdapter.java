package com.trovami.adapters;

import android.app.LauncherActivity;
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

    public NotificationAdapter(Context context, List<String> mSentReq, List<String> mReceivedReq) {
        this.mSentReq = mSentReq;
        this.mReceivedReq = mReceivedReq;
        this.mContext = context;

        //This constructor would switch what to findViewBy according to the type of viewType
//        public ViewHolder(View v, int viewType) {
//            super(v);
//            if (viewType == 0) {
//                name = (TextView) v.findViewById(R.id.name);
//                decsription = (TextView) v.findViewById(R.id.description);
//            } else if (viewType == 1) {
//                place = (TextView) v.findViewById(R.id.place);
//                pics = (ImageView) v.findViewById(R.id.pics);
//            }
//        }

    }

    @NonNull
    @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        ViewHolder vh;
        // create a new view
        switch (viewType) {
            case 1: //sent requests
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                vh = new SentReqViewHolder(v);
                return  vh;
            case 2: //received requests
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                vh = new ReceivedReqViewHolder(v);
//                v.setOnClickListener(new View.OnClickListener(){
//
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, nextActivity.class);
//                        intent.putExtra("ListNo",mRecyclerView.getChildPosition(v));
//                        mContext.startActivity(intent);
//                    }
//                });
                return vh;
        }

//            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
//            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view);
//        if(viewType == SentReq){
//            return new SentReqViewHolder(view);
//        }
//
//        if(viewType == ReceivedReq){
//            return new ReceivedReqViewHolder(view);
//        }
//
        return null;
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        NotificationReq SentReq = mSentReq.get(position);
        NotificationReq ReceivedReq = mReceivedReq.get(position);

        if(position < mSentReq.size()){
            holder.name.setText(SentReq.name);
            Glide.with(mContext)
                    .load(SentReq.photoUrl)
                    .into(holder.photo);
        }
        if(position - mSentReq.size() < mReceivedReq.size()){
            holder.name.setText(ReceivedReq.name);
            Glide.with(mContext)
                    .load(ReceivedReq.photoUrl)
                    .into(holder.photo);
        }


//        holder.from.setText(user.from);
//        holder.to.setText(user.to);
//        holder.status.setText(user.status);
//        holder.name.setText(user.name);
//        Glide.with(mContext)
//                .load(user.photoUrl)
//                .into(holder.photo);

//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: clicked on" + mUnfolllowedUsers.get(position));
//                Toast.makeText(mContext, (CharSequence) mUnfolllowedUsers.get(position), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
//    }

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class SentReqViewHolder extends ViewHolder {

//        TextView from;
//        TextView to;
//        TextView status;
        TextView name;
        ImageView photo;
        RelativeLayout parentLayout;

        public SentReqViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.user_Image);
            name = itemView.findViewById(R.id.user_Name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

    }
    public class ReceivedReqViewHolder extends ViewHolder{

//        TextView from;
//        TextView to;
//        TextView status;
        TextView name;
        ImageView photo;
        RelativeLayout parentLayout;

        public ReceivedReqViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.user_Image);
            name = itemView.findViewById(R.id.user_Name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

    }
}

