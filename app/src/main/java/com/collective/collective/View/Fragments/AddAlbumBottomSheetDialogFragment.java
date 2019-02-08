package com.collective.collective.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.collective.collective.R;
import com.collective.collective.SetupAccountActivity;
import com.collective.collective.View.Utils.ListsUtils;
import com.collective.collective.View.Utils.OnAddAlbum;
import com.collective.collective.View.Utils.OnCropProfilePicture;

import java.util.ArrayList;
import java.util.List;

public class AddAlbumBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static AddAlbumBottomSheetDialogFragment newInstance() {
        return new AddAlbumBottomSheetDialogFragment();
    }

    private List<OnAddAlbum> listeners = new ArrayList<>();

    public void addListener(OnAddAlbum listener) {
        listeners.add(listener);
    }


    void onBottomSheetClick(int type) {
        for (OnAddAlbum listener : listeners) {
            listener.onAddAlbum(type);
        }
    }

    TextView collected;
    TextView wanted;
    TextView loved;
    TextView goToLastFm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_album_bottom_sheet, container,
                false);

        collected = view.findViewById(R.id.add_collected);
        wanted = view.findViewById(R.id.add_wanted);
        loved = view.findViewById(R.id.add_loved);
        goToLastFm = view.findViewById(R.id.add_view_last_fm);

        collected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomSheetClick(ListsUtils.ALBUM_COLLECTED);
            }
        });

        wanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomSheetClick(ListsUtils.ALBUM_WANTED);
            }
        });

        loved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomSheetClick(ListsUtils.ALBUM_LOVED);
            }
        });

        goToLastFm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomSheetClick(ListsUtils.ALBUM_NOT_APPLICABLE);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        addListener((OnAddAlbum) context);
    }
}



