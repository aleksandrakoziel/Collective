package com.collective.collective.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.collective.collective.Model.Last.fm.Artist;
import com.collective.collective.R;
import com.collective.collective.View.Activities.SearchArtistActivity;
import com.collective.collective.View.Adapters.ArtistAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArtistSearchResultsFragment extends Fragment {

    private static final String KEY_ARTISTS = "artists";
    private OnFragmentInteractionListener mListener;
    private List<Artist> mArtists;

    public ArtistSearchResultsFragment() {
    }

    public static ArtistSearchResultsFragment newInstance(List<Artist> artists) {
        ArtistSearchResultsFragment fragment = new ArtistSearchResultsFragment();
        Bundle args = new Bundle();
        // Because my Artist object is parcelable, I can use putParcelableArrayList().
        args.putParcelableArrayList(KEY_ARTISTS, (ArrayList<? extends Parcelable>) artists);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArtists = getArguments().getParcelableArrayList(KEY_ARTISTS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_search_results, container, false);

        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView resultsView = rootView.findViewById(R.id.list_search_results);
        resultsView.setHasFixedSize(true);
        resultsView.setLayoutManager(layout);

        ArtistAdapter artistAdapter = new ArtistAdapter(getActivity(), mArtists, artist -> {
            if (artist != null) {
                mListener = ((OnFragmentInteractionListener) getActivity());
                if (mListener != null) {
                    mListener.onItemSelected(artist);
                }
            }
        });

        resultsView.setAdapter(artistAdapter);

        final SearchArtistActivity activity = (SearchArtistActivity) getActivity();
        FloatingActionButton fab = rootView.findViewById(R.id.fab_search);
        fab.setOnClickListener(view -> activity.searchAgain());

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onItemSelected(Artist artist);
    }
}
