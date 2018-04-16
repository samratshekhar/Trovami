package com.trovami.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.trovami.R;
import com.trovami.models.HomeGroup;

import java.util.List;

/**
 * Created by anush on 15-04-2018.
 */

public class HomeRecycleExpandableAdapater extends ExpandableRecyclerAdapter<HomeGroup,String,HomeGroupViewHolder,HomeItemViewHolder>{

    private LayoutInflater mInflater;

    public HomeRecycleExpandableAdapater(Context context, @NonNull List<HomeGroup> homeGroups) {
        super(homeGroups);
        mInflater = LayoutInflater.from(context);
    }

    // onCreate ...
    @Override
    public HomeGroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View groupView = mInflater.inflate(R.layout.list_group, parentViewGroup, false);
        return new HomeGroupViewHolder(groupView);
    }

    @Override
    public HomeItemViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View itemView = mInflater.inflate(R.layout.list_item, childViewGroup, false);
        return new HomeItemViewHolder(itemView);
    }

    // onBind ...
    @Override
    public void onBindParentViewHolder(@NonNull HomeGroupViewHolder hgViewHolder, int parentPosition, @NonNull HomeGroup hg) {
        hgViewHolder.bind(hg);
    }

    @Override
    public void onBindChildViewHolder(@NonNull HomeItemViewHolder itemViewHolder, int parentPosition, int childPosition, @NonNull String item) {
        itemViewHolder.bind(item);
    }
}
