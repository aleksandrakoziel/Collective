package com.collective.collective.View.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.collective.collective.GlideApp;
import com.collective.collective.Model.Firestore.Following;
import com.collective.collective.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class FollowingsRecyclerViewAdapter extends FirestoreRecyclerAdapter<Following, FollowingsRecyclerViewAdapter.FollowingsHolder> {

    Context context;

    public FollowingsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Following> options) {
        super(options);
    }

    @NonNull
    @Override
    public FollowingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_item, parent, false);
        return new FollowingsHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull FollowingsHolder holder, int position, @NonNull Following model) {
        holder.usernameTextView.setText(model.getUsername());
        loadProfilePicture(model.getUsername(), holder.profilePictureImageView);
    }

    private void loadProfilePicture(String username, CircleImageView profilePicture) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/users/" + username + ".jpg");
        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> loadProfilePictureIntoImageView(storageReference, true, profilePicture))
                .addOnFailureListener(e -> {
                    Log.e("ACCOUNT", "Cannot load image with error: ", e);
                    loadProfilePictureIntoImageView(storageReference, false, profilePicture);
                });
    }

    private void loadProfilePictureIntoImageView(StorageReference storageReference, boolean isResourceExist, CircleImageView profilePicture) {
        if (isResourceExist) {
            GlideApp.with(context)
                    .load(storageReference)
                    .into(profilePicture);
        } else {
            profilePicture.setImageResource(R.drawable.cd);
        }
    }

    class FollowingsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.following_username_text)
        TextView usernameTextView;
        @BindView(R.id.following_image_view)
        CircleImageView profilePictureImageView;

        public FollowingsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }
    }
}