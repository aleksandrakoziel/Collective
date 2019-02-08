package com.collective.collective.View.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.collective.collective.Model.Last.fm.Album;
import com.collective.collective.Model.Last.fm.Artist;
import com.collective.collective.Model.Last.fm.Image;
import com.collective.collective.Model.SearchServiceLastFm;
import com.collective.collective.R;
import com.collective.collective.View.Adapters.DiscographyAdapter;
import com.collective.collective.View.Fragments.AddAlbumBottomSheetDialogFragment;
import com.collective.collective.View.Utils.AlbumStorageTypeDialog;
import com.collective.collective.View.Utils.ListsUtils;
import com.collective.collective.View.Utils.OnAddAlbum;
import com.collective.collective.View.Utils.OnShowAddDialog;
import com.collective.collective.View.Utils.OnStorageType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DiscographyActivity extends AppCompatActivity implements OnAddAlbum, OnShowAddDialog, OnStorageType {
    public static final String ARTISTS_ID = "mbid";
    private static final String LOG_TAG = "DISCOGRAPHY";
    private static Artist artist;
    private Album currentAlbum;
    private String currentList;
    private AddAlbumBottomSheetDialogFragment addAlbumBottomSheetDialogFragment;
    private AlbumStorageTypeDialog albumStorageTypeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discography);

        artist = getIntent().getParcelableExtra(ARTISTS_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        String actionTitle = artist.getName();
        toolbar.setTitle(actionTitle);
        setSupportActionBar(toolbar);

        ImageView artistImage = findViewById(R.id.artist_image_imageview);

        if (!artist.getImage().isEmpty()) {
            List<Image> images = artist.getImage();
            Image largeImage = images.get(3);
            if (largeImage != null && !largeImage.getText().equals("")) {
                Uri largeImageUri = Uri.parse(largeImage.getText());
                Picasso.with(this).cancelRequest(artistImage);
                if (largeImageUri != null) {
                    Picasso
                            .with(this)
                            .load(largeImageUri)
                            .into(artistImage);
                } else {
                    artistImage.setImageResource(R.drawable.cd);
                }
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), SearchArtistActivity.class);
                startActivity(intent);
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add the similar artists fragment.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.discography_fragment_container, new DiscographyFragment())
                    .commit();
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("ADD");
        if (fragment == null) {
            addAlbumBottomSheetDialogFragment = new AddAlbumBottomSheetDialogFragment();
        } else {
            addAlbumBottomSheetDialogFragment = (AddAlbumBottomSheetDialogFragment) fragment;
        }

        albumStorageTypeDialog = new AlbumStorageTypeDialog(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public void onAddAlbum(int listType) {
        addAlbumBottomSheetDialogFragment.dismiss();

        switch (listType) {
            case ListsUtils.ALBUM_COLLECTED:
                currentList = ListsUtils.ALBUM_COLLECTED_NAME;
                albumStorageTypeDialog.show();
                break;
            case ListsUtils.ALBUM_WANTED:
                currentList = ListsUtils.ALBUM_WANTED_NAME;
                albumStorageTypeDialog.show();
                break;
            case ListsUtils.ALBUM_LOVED:
                saveAlbumOnList(ListsUtils.ALBUM_LOVED_NAME, false, false, false, false);
                break;
            case ListsUtils.ALBUM_NOT_APPLICABLE:
                goToLastFm();
                break;
            default:
                break;
        }
    }

    private void saveAlbumOnList(String listName, boolean isCd, boolean isCassette, boolean isVinyl, boolean isDigital) {
        if (currentAlbum != null) {
            Map<String, Object> albumData = new HashMap<>();
            albumData.put("mbid", currentAlbum.getMbid());
            albumData.put("artist", artist.getName());
            albumData.put("title", currentAlbum.getName());
            albumData.put("cassette", isCassette ? "true" : "false");
            albumData.put("cd", isCd ? "true" : "false");
            albumData.put("vinyl", isVinyl ? "true" : "false");
            albumData.put("cloud", isDigital ? "true" : "false");
            albumData.put("image", currentAlbum.getImageMedium());

            String documentName = currentAlbum.getMbid();
            if (documentName == null) {
                documentName = artist.getName() + currentAlbum.getName();
            }

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            String firebaseAuth = FirebaseAuth.getInstance().getUid();
            if (firebaseAuth != null) {
                CollectionReference userReference = firebaseFirestore.collection("users");
                firebaseFirestore.collection("users").document(firebaseAuth).collection(listName)
                        .document(documentName)
                        .set(albumData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(LOG_TAG, "DocumentSnapshot successfully written!");

                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar snackbar = Snackbar.make(parentLayout,
                                    "Album " + currentAlbum.getName() + " by " + artist.getName()
                                            + " was successfully added to your " + listName + " list!",
                                    Snackbar.LENGTH_LONG).setAction("VIEW PROFILE", view -> {
                                        Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                                        intent.putExtra("username_uid", FirebaseAuth.getInstance().getUid());
                                        startActivity(intent);
                                    });
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            snackbar.show();
                        })
                        .addOnFailureListener(e -> Log.w(LOG_TAG, "Error writing document", e));

                /*Uri imageUri = (currentAlbum.getImageMedium() != null) ? Uri.parse(currentAlbum.getImageMedium()) : null;

                if (imageUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference storageReference = storageRef.child("images/users/" + listName + ".jpg");
                    storageReference.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> Log.d(LOG_TAG, "Image successfully written!"))
                            .addOnFailureListener(e ->
                                    Toast.makeText(getApplicationContext(),
                                            "Cannot upload profile picture.",
                                            Toast.LENGTH_SHORT).show())
                    ;
                }*/
            }
        }
    }

    private void goToLastFm() {
        Uri uri = (currentAlbum.getUrl() != null) ? Uri.parse(currentAlbum.getUrl()) : null;
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "Couldn't call " + uri.toString() + ", no receiving apps installed!");
            }
        }
    }

    @Override
    public void onShowAddDialog(Album album) {
        currentAlbum = album;
        addAlbumBottomSheetDialogFragment.show(getSupportFragmentManager(), "ADD");
    }

    @Override
    public void onStorageType(boolean cassette, boolean cd, boolean vinyl, boolean cloud) {
        if (currentList != null) {
            saveAlbumOnList(currentList, cd, cassette, vinyl, cloud);
        }
    }

    public static class DiscographyFragment extends Fragment {
        private static final long TIMEOUT_SECONDS = 100;
        private static final String LOG_TAG = DiscographyFragment.class.getSimpleName();
        private CompositeSubscription mCompositeSubscription;
        private SearchServiceLastFm searchService;
        private List<Album> mAlbums;
        RecyclerView resultsView;
        private DiscographyAdapter discographyAdapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_discography, container, false);

            mCompositeSubscription = new CompositeSubscription();

            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            layout.setOrientation(LinearLayoutManager.VERTICAL);
            resultsView = rootView.findViewById(R.id.list_albums);
            resultsView.setHasFixedSize(true);
            resultsView.setLayoutManager(layout);
            resultsView.setVisibility(View.GONE);

            // On Long Click persist this artist to the database.
            discographyAdapter = new DiscographyAdapter(getActivity(), mAlbums, uri -> {
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "Couldn't call " + uri.toString() + ", no receiving apps installed!");
                    }
                }
            }, album -> Toast.makeText(getActivity(), "Artist saved.", Toast.LENGTH_SHORT).show());
            resultsView.setAdapter(discographyAdapter);

            loadAlbums();
            return rootView;
        }

        @Override
        public void onDestroy() {
            mCompositeSubscription.unsubscribe();
            super.onDestroy();
        }

        private void loadAlbums() {
            String mbid = artist.getMbid();
            searchService = new SearchServiceLastFm();
            Observable<List<Album>> fetchDataObservable = searchService.getAlbumsArtists(mbid);

            mCompositeSubscription.add(fetchDataObservable
                    .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Album>>() {

                        @Override
                        public void onNext(List<Album> artists) {
                            mAlbums = artists;
                            discographyAdapter.setAlbumList(artists);
                        }

                        @Override
                        public void onCompleted() {
                            resultsView.setVisibility(View.VISIBLE);
                            RelativeLayout layout = getActivity().findViewById(R.id.fab_loading);
                            layout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Download Error", Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
    }
}