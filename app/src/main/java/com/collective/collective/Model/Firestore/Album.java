package com.collective.collective.Model.Firestore;


public class Album {
    private String artist;
    private String title;
    private String mbid;
    private String image;
    private String cassette;
    private String cd;
    private String vinyl;
    private String cloud;

    public Album() {
    }

    public Album(String artist, String title, String mbid, String image, String cassette, String cd, String vinyl, String cloud) {
        this.artist = artist;
        this.title = title;
        this.mbid = mbid;
        this.image = image;
        this.cassette = cassette;
        this.cd = cd;
        this.vinyl = vinyl;
        this.cloud = cloud;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public boolean isCassette() {

        return cassette.equals("true");
    }

    public void setCassette(String cassette) {
        this.cassette = cassette;
    }

    public boolean isCd() {
        return cd.equals("true");
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public boolean isVinyl() {
        return vinyl.equals("true");
    }

    public void setVinyl(String vinyl) {
        this.vinyl = vinyl;
    }

    public boolean isCloud() {
        return cloud.equals("true");
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }
}
