package com.trovami.utils;

import android.app.Application;

import com.trovami.models.User;

/**
 * Created by samrat on 08/05/18.
 */

public class ApplicationState extends Application {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
