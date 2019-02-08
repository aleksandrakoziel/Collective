package com.collective.collective.View.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.collective.collective.R;
import com.collective.collective.View.Activities.SearchArtistActivity;

public class ArtistSearchFragment extends Fragment {

    public ArtistSearchFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);

        final EditText editText = rootView.findViewById(R.id.artist_search);
        final SearchArtistActivity activity = (SearchArtistActivity) getActivity();

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            String artist = editText.getText().toString();
            if (activity != null) {
                activity.searchArtists(artist);
            }
        });

        return rootView;
    }
}

