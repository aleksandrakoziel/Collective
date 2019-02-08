package com.collective.collective.View.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.collective.collective.Model.Last.fm.Artist;
import com.collective.collective.Model.Last.fm.Image;
import com.collective.collective.Model.SearchServiceLastFm;
import com.collective.collective.R;
import com.collective.collective.UserActivity;
import com.collective.collective.View.Fragments.ArtistSearchFragment;
import com.collective.collective.View.Fragments.ArtistSearchResultsFragment;
import com.collective.collective.View.Utils.AccountDataUtils;
import com.google.firebase.auth.FirebaseAuth;

public class SearchArtistActivity extends AppCompatActivity
        implements ArtistSearchResultsFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String SEARCH_FRAGMENT_TAG = "SFTAG";
    private static final String RESULTS_FRAGMENT_TAG = "RFTAG";
    private ArtistSearchFragment searchFragment;
    private MenuItem searchActionProgressItem;
    private ProgressBar progressBar;

    private String[] mActivitiesList;
    private DrawerLayout drawerLayout;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        setSupportActionBar(toolbar);

        mActivitiesList = getResources().getStringArray(R.array.activity_names);
        drawerLayout = findViewById(R.id.drawer_layout);

        mTitle = mDrawerTitle = getTitle();
        drawerLayout = findViewById(R.id.drawer_layout);
        setNavigationViewListener();
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);

        searchFragment = new ArtistSearchFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_fragment_container, searchFragment, SEARCH_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchActionProgressItem = menu.findItem(R.id.searchActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void showProgressBar() {
        searchActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        searchActionProgressItem.setVisible(false);
    }

    public void searchArtists(String artist) {
        showProgressBar();

        final SearchServiceLastFm searchService = new SearchServiceLastFm();

        searchService
                .mWebService.searchArtists(artist)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchServiceLastFm.ResultsDataEnvelope>() {

                    ArtistSearchResultsFragment resultsFragment;

                    @Override
                    public final void onCompleted() {
                        hideProgressBar();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.search_fragment_container, resultsFragment, RESULTS_FRAGMENT_TAG)
                                .commit();
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "Download Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(SearchServiceLastFm.ResultsDataEnvelope resultsDataEnvelope) {

                        ArrayList<Artist> artists =
                                new ArrayList<>();

                        for (Artist data : resultsDataEnvelope.results.getArtistmatches().getArtist()) {
                            ArrayList<Image> images =
                                    new ArrayList<>();
                            for (Image image_data : data.getImage()) {
                                Image image = new Image(
                                        image_data.getText(),
                                        image_data.getSize(),
                                        image_data.getAdditionalProperties());
                                images.add(image);
                            }

                            Artist artist = new Artist(
                                    data.getName(),
                                    data.getListeners(),
                                    data.getMbid(),
                                    data.getUrl(),
                                    data.getStreamable(),
                                    images,
                                    data.getAdditionalProperties());
                            artists.add(artist);
                        }
                        // Instantiate the results fragment with the artists list.
                        resultsFragment = ArtistSearchResultsFragment.newInstance(artists);
                    }
                });
    }

    /**
     * Function that switches out results fragment for a search fragment so the user can search
     * again.
     */
    public void searchAgain() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_fragment_container, searchFragment, SEARCH_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onItemSelected(Artist artist) {
        // Selecting an item in the search results launches the similar artists activity.
        Intent intent = new Intent(this, DiscographyActivity.class);
        intent.putExtra(DiscographyActivity.ARTISTS_ID, artist);
        startActivity(intent);

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps activities in the main content view
     */
    private void selectItem(int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, SearchArtistActivity.class);
                break;/*
            case 1:
               // intent = new Intent(this, SavedArtistsActivity.class);
                break;*/
            default:
                intent = new Intent(this, SearchArtistActivity.class);
                break;
        }
        startActivity(intent);
        setTitle(mActivitiesList[position]);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account: {
                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                intent.putExtra("username_uid", FirebaseAuth.getInstance().getUid());
                startActivity(intent);
                break;
            }
            case R.id.nav_settings: {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_artist: {
                Intent intent = new Intent(getApplicationContext(), SearchArtistActivity.class);
                startActivity(intent);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameDrawer = headerView.findViewById(R.id.drawer_username);
        usernameDrawer.setText(AccountDataUtils.getAccountUsername(this));

        CircleImageView profileDrawer = headerView.findViewById(R.id.drawer_profile_picture);
        Bitmap profilePicture = AccountDataUtils.loadProfilePictureStorage(this);
        if (profilePicture != null && profileDrawer != null) {
            profileDrawer.setImageBitmap(profilePicture);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mTitle);
        }
    }
}
