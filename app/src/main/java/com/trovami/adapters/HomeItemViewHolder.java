package com.trovami.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.trovami.R;

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
        layoutListParent=itemView.findViewById(R.id.linear_ItemParent);;
    }

    public void bind(String uid) {
        txtItemTitle.setText(uid);
    }
}
