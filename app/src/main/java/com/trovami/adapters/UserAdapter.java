package com.trovami.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trovami.R;
import com.trovami.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TULIKA on 16-Apr-18.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private List<User> mUnfolllowedUsers = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private UserActionListener mlistener;

    public UserAdapter(Context context, List<User> mUnfolllowedUsers, UserActionListener listener) {
        this.mUnfolllowedUsers = mUnfolllowedUsers;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mlistener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_user_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        final User user = mUnfolllowedUsers.get(position);
        holder.setViewHolder(user);
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onActionClicked(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUnfolllowedUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        ImageView profilePic;
        Button addButton;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.user_item_title_text_view);
            subtitle = itemView.findViewById(R.id.user_item_subtitle_text_view);
            profilePic = itemView.findViewById(R.id.user_item_profile_image_view);
            addButton = itemView.findViewById(R.id.user_item_add_button);

        }

        public void clearViewHolder() {
            title.setText(null);
            subtitle.setText(null);
        }

        public void setViewHolder(User user) {
            title.setText(user.name);
            subtitle.setText(user.email);
            Glide.with(mContext)
                    .load(user.photoUrl)
                    .into(profilePic);

        }
    }

    public interface UserActionListener {
        void onActionClicked(User user);
    }
}
