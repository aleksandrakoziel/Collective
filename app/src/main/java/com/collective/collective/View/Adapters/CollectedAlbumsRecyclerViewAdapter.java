package com.collective.collective.View.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.collective.collective.Model.Firestore.Album;
import com.collective.collective.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CollectedAlbumsRecyclerViewAdapter extends FirestoreRecyclerAdapter<Album, CollectedAlbumsRecyclerViewAdapter.AlbumHolder> {

    private Context context;


    public CollectedAlbumsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Album> options) {
        super(options);
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new CollectedAlbumsRecyclerViewAdapter.AlbumHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectedAlbumsRecyclerViewAdapter.AlbumHolder holder,
                                    int position, @NonNull Album model) {
        holder.artistTextView.setText(model.getArtist());
        holder.albumTextView.setText(model.getTitle());
        holder.imageCassette.setVisibility(model.isCassette() ? View.VISIBLE : View.INVISIBLE);
        holder.imageCd.setVisibility(model.isCd() ? View.VISIBLE : View.INVISIBLE);
        holder.imageVinyl.setVisibility(model.isVinyl() ? View.VISIBLE : View.INVISIBLE);
        holder.imageCloud.setVisibility(model.isCloud() ? View.VISIBLE : View.INVISIBLE);
        loadCover(model.getImage(), holder.coverImageView);
    }

    private void loadCover(String url, CircleImageView cover) {
        Uri uri = (url != null) ? Uri.parse(url) : null;
        Picasso.with(context).cancelRequest(cover);
        if (uri != null) {
            Picasso
                    .with(context)
                    .load(uri)
                    .into(cover);
        } else {
            cover.setImageResource(R.drawable.cd);
        }
    }

    class AlbumHolder extends ViewHolder {
        @BindView(R.id.artist_name)
        TextView artistTextView;
        @BindView(R.id.album_name)
        TextView albumTextView;
        @BindView(R.id.album_cover_view)
        CircleImageView coverImageView;
        @BindView(R.id.image_cassette)
        ImageView imageCassette;
        @BindView(R.id.image_cd)
        ImageView imageCd;
        @BindView(R.id.image_vinyl)
        ImageView imageVinyl;
        @BindView(R.id.image_cloud)
        ImageView imageCloud;

        public AlbumHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }
    }


}
