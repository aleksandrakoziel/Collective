package com.collective.collective.Model;

import com.collective.collective.BuildConfig;
import com.collective.collective.Model.Last.fm.Album;
import com.collective.collective.Model.Last.fm.Image;
import com.collective.collective.Model.Last.fm.Results;
import com.collective.collective.Model.Last.fm.Topalbums;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchServiceLastFm {
    private static final String API_KEY = BuildConfig.LAST_FM_API_KEY;
    public WebServiceLastFm mWebService;

    public SearchServiceLastFm() {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.LAST_FM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
        mWebService = retrofit.create(WebServiceLastFm.class);
    }

    public interface WebServiceLastFm {
        @GET("?method=artist.search&format=json&api_key=" + API_KEY)
        Observable<ResultsDataEnvelope> searchArtists(@Query("artist") String name);

        @GET("?method=artist.gettopalbums&format=json&api_key=" + API_KEY)
        Observable<TopalbumsDataEnvelope> getAlbumsArtists(@Query("mbid") String mbid);

        @GET("?method=album.search&format=json&api_key=" + API_KEY)
        Observable<ResultsDataEnvelope> searchAlbums(@Query("album") String name);
    }

    /**
     * Base class for results returned by the artist search web service.
     */
    public class ResultsDataEnvelope {
        public Results results;
    }

    /**
     * Base class for similar artists returned by the artist search web service.
     */
    private class TopalbumsDataEnvelope {
        @SerializedName("topalbums")
        @Expose
        private Topalbums topalbums;

        Observable<? extends TopalbumsDataEnvelope> filterWebServiceErrors() {
            if (topalbums.getAlbums() != null) {
                return Observable.just(this);
            }
            return null;
        }
    }

    public Observable<List<Album>> getAlbumsArtists(final String mbid) {
        return mWebService.getAlbumsArtists(mbid)
                .flatMap((Func1<TopalbumsDataEnvelope, Observable<? extends TopalbumsDataEnvelope>>)
                        TopalbumsDataEnvelope::filterWebServiceErrors).map(listData -> {
                    final ArrayList<Album> search_results =
                            new ArrayList<>();

                    for (Album data : listData.topalbums.getAlbums()) {
                        ArrayList<Image> images =
                                new ArrayList<>();
                        for (Image image_data : data.getImage()) {
                            Image image = new Image(
                                    image_data.getText(),
                                    image_data.getSize(),
                                    image_data.getAdditionalProperties());
                            images.add(image);
                        }
                        final Album album = new Album(
                                images,
                                data.getMbid(),
                                data.getListeners(),
                                data.getName(),
                                data.getRank(),
                                data.getUrl());
                        search_results.add(album);
                    }

                    return search_results;
                });
    }
}
