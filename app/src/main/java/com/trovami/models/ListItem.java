package com.trovami.models;

/**
 * Created by anush on 07-04-2018.
 */

public class ListItem {
    public String photoUrl;
    public String title;
    public String subtitle;

    public ListItem(){
        //empty constructor
    }

    public ListItem(String in_photoUrl,String in_title, String in_subtitle){
        this.photoUrl=in_photoUrl;
        this.title=in_title;
        this.subtitle = in_subtitle;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public String getTitle(){
        return title;
    }

    public String getSubtitle(){
        return subtitle;
    }
}
