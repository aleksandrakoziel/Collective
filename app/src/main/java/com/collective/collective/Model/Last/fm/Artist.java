package com.collective.collective.Model.Last.fm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;


@Generated("org.jsonschema2pojo")
public class Artist extends SugarRecord implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("listeners")
    @Expose
    private String listeners;
    @SerializedName("mbid")
    @Expose
    private String mbid;
    @SerializedName("match")
    @Expose
    private String match;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("streamable")
    @Expose
    private String streamable;
    @SerializedName("image")
    @Expose
    @Ignore
    private List<Image> image = new ArrayList<>();
    @Ignore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Expose
    private String imageMedium;

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

    public Artist() {
    }

    public Artist(String name, String listeners, String mbid,
                  String url, String streamable, List<Image> image,
                  Map<String, Object> additionalProperties) {
        this.name = name;
        this.listeners = listeners;
        this.mbid = mbid;
        this.url = url;
        this.streamable = streamable;
        this.image = image;
        this.additionalProperties = additionalProperties;
        // After all the images are in, set the ImageMedium property.
        this.setImageMedium(image);
    }

    public Artist(String name, String listeners, String mbid,
                  String url, String streamable, List<Image> image,
                  Map<String, Object> additionalProperties, String match) {
        this(name, listeners, mbid, url, streamable, image, additionalProperties);
        this.match = match;
    }

    public Artist(Parcel parcel) {
        name = parcel.readString();
        listeners = parcel.readString();
        mbid = parcel.readString();
        url = parcel.readString();
        streamable = parcel.readString();
        parcel.readTypedList(image, Image.CREATOR);
        additionalProperties = parcel.readHashMap(ClassLoader.getSystemClassLoader());
        match = parcel.readString();
        imageMedium = parcel.readString();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist withName(String name) {
        this.name = name;
        return this;
    }

    public String getListeners() {
        return listeners;
    }

    public void setListeners(String listeners) {
        this.listeners = listeners;
    }

    public Artist withListeners(String listeners) {
        this.listeners = listeners;
        return this;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public Artist withMbid(String mbid) {
        this.mbid = mbid;
        return this;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Artist withUrl(String url) {
        this.url = url;
        return this;
    }

    public String getStreamable() {
        return streamable;
    }

    public void setStreamable(String streamable) {
        this.streamable = streamable;
    }

    public Artist withStreamable(String streamable) {
        this.streamable = streamable;
        return this;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List<Image> image) {
        this.image = image;
    }

    public Artist withImage(List<Image> image) {
        this.image = image;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Artist withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public String getImageMedium() {
        return imageMedium;
    }

    private void setImageMedium(List<Image> image) {
        for(Image obj : image) {
            if (obj.getSize().equals("medium")
                    && !obj.getText().equals("")) {
                this.imageMedium = obj.getText();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.name);
        parcel.writeString(this.listeners);
        parcel.writeString(this.mbid);
        parcel.writeString(this.url);
        parcel.writeString(this.streamable);
        parcel.writeTypedList(this.image);
        parcel.writeMap(this.additionalProperties);
        parcel.writeString(this.match);
        parcel.writeString(this.imageMedium);
    }
}
