package com.collective.collective.Model.Firestore;

/**
 * Created by Aleksandra on 15.01.2019.
 */

public class Following {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    public Following(){}

    public Following(String username) {
        this.username = username;
    }
}
