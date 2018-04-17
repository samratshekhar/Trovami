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
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TULIKA on 16-Apr-18.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private static final String TAG = "UserAdapter";
    private List<User> mUnfolllowedUsers = new ArrayList<>();
    private Context mContext;

    public UserAdapter(Context context, List<User> mUnfolllowedUsers) {
        this.mUnfolllowedUsers = mUnfolllowedUsers;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        User user = mUnfolllowedUsers.get(position);


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
        return mUnfolllowedUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView uid;
        TextView name;
        TextView email;
        TextView phone;
        TextView gender;
        ImageView photo;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.user_Image);
            name = itemView.findViewById(R.id.user_Name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
