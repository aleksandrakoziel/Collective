package com.collective.collective.Model.Last.fm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Album extends SugarRecord implements Parcelable {

    @SerializedName("image")
    @Expose
    @Ignore
    private ArrayList<Image> image;

    @SerializedName("mbid")
    @Expose
    private String mbid;

    @SerializedName("listeners")
    @Expose
    private String listeners;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("rank")
    @Expose
    private String rank;

    @SerializedName("url")
    @Expose
    private String url;

    @Expose
    private String imageMedium;

    public Album(ArrayList<Image> image, String mbid, String listeners, String name, String rank, String url) {
        this.image = image;
        this.mbid = mbid;
        this.listeners = listeners;
        this.name = name;
        this.rank = rank;
        this.url = url;
        setImageMedium(image);
    }

    public Album() {

    }

    public ArrayList<Image> getImage() {
        return image;
    }

    public void setImage(ArrayList<Image> image) {
        this.image = image;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getListeners() {
        return listeners;
    }

    public void setListeners(String listeners) {
        this.listeners = listeners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ClassPojo [image = " + image +
                ", mbid = " + mbid +
                ", listeners = " + listeners +
                ", name = " + name +
                ", rank = " + rank +
                ", url = " + url + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.listeners);
        parcel.writeString(this.mbid);
        parcel.writeString(this.url);
        parcel.writeTypedList(this.image);
        parcel.writeString(this.rank);
    }


    public String getImageMedium() {
        return imageMedium;
    }

    /**
     *
     * @param image
     *     The image
     */
    private void setImageMedium(List<Image> image) {
        for(Image obj : image) {
            if (obj.getSize().equals("medium")
                    && !obj.getText().equals("")) {
                this.imageMedium = obj.getText();
            }
        }
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {

        @Override
        public Artist createFromParcel(Parcel parcel) {
            return new Artist(parcel);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
