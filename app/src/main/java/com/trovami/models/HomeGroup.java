package com.trovami.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by anush on 15-04-2018.
 */

public class HomeGroup implements Parent<String> {

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private String groupName;
    private List<String> userIDs;

    public HomeGroup(String name, List<String> uids) {
        userIDs = uids;
        groupName=name;
    }

    @Override
    public List<String> getChildList() {
            return userIDs;
    }

    public String getName(){
        return groupName;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}

