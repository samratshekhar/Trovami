package com.trovami.adapters;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.trovami.R;
import com.trovami.models.HomeGroup;

/**
 * Created by anush on 15-04-2018.
 */

public class HomeGroupViewHolder extends ParentViewHolder {
    private TextView txtGroupTitle;

    public HomeGroupViewHolder(View itemView) {
        super(itemView);
        txtGroupTitle = itemView.findViewById(R.id.home_group_title_text_view);
    }

    public void bind(HomeGroup hg) {
        txtGroupTitle.setText(hg.getName());
    }
}
