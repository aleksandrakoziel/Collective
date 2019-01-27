package com.collective.collective.Model.Firestore;


import java.util.List;

public class User {
    private String username;
    private String description;
    private List<Album> lovedAlbums;
    private List<Album> wantedAlbums;
    private List<Album> collectedAlbums;
    private List<User> followings;


    public User(String description, String username) {
        this.username = username;
        this.description = description;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
