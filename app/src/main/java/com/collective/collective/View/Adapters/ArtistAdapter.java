package com.collective.collective.View.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collective.collective.Model.Last.fm.Artist;
import com.collective.collective.Model.Last.fm.Image;
import com.collective.collective.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private final Context mContext;
    private List<Artist> artistList;
    private final OnItemClickListener listener;

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        final ImageView artist_image;
        final TextView artist_name;
        final LinearLayout item;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            artist_image = itemView.findViewById(R.id.artist_image_imageview);
            artist_name = itemView.findViewById(R.id.artist_name_textview);
            item = (LinearLayout) itemView;
        }
    }

    public ArtistAdapter(Context mContext, List<Artist> artists, OnItemClickListener listener) {
        this.mContext = mContext;
        this.artistList = artists;
        this.listener = listener;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.artist_list_item, parent, false);

        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        final Artist artist = artistList.get(position);
        Uri image_uri = null;

        List<Image> images = artist.getImage();
        if (images.size() != 0 && !images.get(1).getText().equals("")) {
            image_uri  = Uri.parse(images.get(1).getText());
        }

        Picasso.with(mContext).cancelRequest(holder.artist_image);
        if (image_uri != null) {
            Picasso
                    .with(mContext)
                    .load(image_uri)
                    .into(holder.artist_image);
        } else {
            holder.artist_image.setImageResource(R.drawable.cd);
        }

        holder.artist_name.setText(artist.getName());
        holder.artist_image.setContentDescription(artist.getName());

        holder.item.setOnClickListener(v -> listener.onItemClick(artist));

    }

    @Override
    public int getItemCount() {
        if(artistList != null) {
            return artistList.size();
        }
        return 0;
    }

    public void setArtistList(List<Artist> artists) {
        this.artistList = artists;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Artist artist);
    }
}

