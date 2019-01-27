package com.collective.collective.Model.Firestore;

/**
 * Created by Aleksandra on 14.01.2019.
 */

public class Album {
    private String artist;
    private String name;
    private int year;
    private String mbid;
    private boolean cassette;
    private boolean cd;
    private boolean vinyl;
    private boolean cloud;

    public Album() {

    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public boolean isCassette() {
        return cassette;
    }

    public void setCassette(boolean cassette) {
        this.cassette = cassette;
    }

    public boolean isCd() {
        return cd;
    }

    public void setCd(boolean cd) {
        this.cd = cd;
    }

    public boolean isVinyl() {
        return vinyl;
    }

    public void setVinyl(boolean vinyl) {
        this.vinyl = vinyl;
    }

    public boolean isCloud() {
        return cloud;
    }

    public void setCloud(boolean cloud) {
        this.cloud = cloud;
    }
}
