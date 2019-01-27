package com.collective.collective.View.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.collective.collective.View.Utils.OnCropProfilePicture;
import com.collective.collective.R;
import com.collective.collective.SetupAccountActivity;

import java.util.ArrayList;
import java.util.List;


public class AddAvatarBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static AddAvatarBottomSheetDialogFragment newInstance() {
        return new AddAvatarBottomSheetDialogFragment();
    }

    private List<OnCropProfilePicture> listeners = new ArrayList<OnCropProfilePicture>();

    public void addListener(OnCropProfilePicture listener) {
        listeners.add(listener);
    }
    void onBottomSheetClick(int type){
        for(OnCropProfilePicture listener : listeners){
            listener.onCropProfilePicture(type);
        }
    }

    TextView useCamera;
    TextView useGallery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_avatar_bottom_sheet, container,
                false);

        useCamera = view.findViewById(R.id.add_photo_camera);
        useGallery = view.findViewById(R.id.add_photo_gallery);

        useGallery.setOnClickListener(view12 -> onBottomSheetClick(SetupAccountActivity.GALLERY_PICK));

        useCamera.setOnClickListener(view1 -> onBottomSheetClick(SetupAccountActivity.CAMERA_PICK));

        return view;
    }
}