package com.collective.collective.Model.Last.fm;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Topalbums {

    @SerializedName("album")
    @Expose
    private List<Album> albums = new ArrayList<>();


    public Topalbums() {
    }

    public Topalbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
