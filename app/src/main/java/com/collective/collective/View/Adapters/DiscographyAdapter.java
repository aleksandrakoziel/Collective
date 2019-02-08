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

import com.collective.collective.Model.Last.fm.Album;
import com.collective.collective.Model.Last.fm.Image;
import com.collective.collective.R;
import com.collective.collective.View.Utils.OnShowAddDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiscographyAdapter extends RecyclerView.Adapter<DiscographyAdapter.AlbumViewHolder> {
    private final Context mContext;
    private List<Album> albumList;
    private final OnItemClickListener listener;
    private final OnItemLongClickListener listenerlong;
    private OnShowAddDialog onShowAddDialog;

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        final ImageView artist_image;
        final TextView artist_name;
        final LinearLayout item;

        AlbumViewHolder(View itemView) {
            super(itemView);
            artist_image = itemView.findViewById(R.id.album_image_imageview);
            artist_name = itemView.findViewById(R.id.album_name_textview);
            item = (LinearLayout) itemView;
        }
    }

    public DiscographyAdapter(Context mContext, List<Album> albums,
                              OnItemClickListener listener, OnItemLongClickListener listenerlong) {
        this.mContext = mContext;
        this.onShowAddDialog = (OnShowAddDialog) mContext;
        this.albumList = albums;
        this.listener = listener;
        this.listenerlong = listenerlong;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.album_list_item, parent, false);

        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        final Album album = albumList.get(position);
        Uri image_uri = null;
        final Uri artist_uri = (album.getUrl() != null)? Uri.parse(album.getUrl()): null ;

        List<Image> images = album.getImage();
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

        holder.artist_name.setText(album.getName());
        holder.artist_image.setContentDescription(album.getName());

        holder.item.setOnClickListener(v -> onShowAddDialog.onShowAddDialog(album));
        holder.item.setOnLongClickListener(v -> {
            listenerlong.onItemLongClick(album);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        if(albumList != null) {
            return albumList.size();
        }
        return 0;
    }

    public void setAlbumList(List<Album> albums) {
        this.albumList = albums;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Uri artist_uri);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Album album);
    }
}
