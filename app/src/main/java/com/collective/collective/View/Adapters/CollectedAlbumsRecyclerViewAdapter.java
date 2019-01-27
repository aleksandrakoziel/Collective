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
import com.collective.collective.UserAlbumsFragment.OnListFragmentInteractionListener;
import com.collective.collective.View.Fragments.FollowingsFragment;
import com.collective.collective.dummy.DummyContent.DummyItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CollectedAlbumsRecyclerViewAdapter extends RecyclerView.Adapter<CollectedAlbumsRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public CollectedAlbumsRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
     * specified {@link FollowingsFragment.OnListFragmentInteractionListener}.
     * TODO: Replace the implementation with code for your data type.
     */
    public static class FollowingsRecyclerViewAdapter extends FirestoreRecyclerAdapter<Following, FollowingsRecyclerViewAdapter.FollowingsHolder> {

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
}
