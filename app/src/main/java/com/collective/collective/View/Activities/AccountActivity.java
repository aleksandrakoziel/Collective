package com.collective.collective.View.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.collective.collective.AccountInfoFragment;
import com.collective.collective.Model.Firestore.Album;
import com.collective.collective.UserActivity;
import com.collective.collective.UserAlbumsFragment2;
import com.collective.collective.View.Fragments.FollowingsFragment;
import com.collective.collective.R;
import com.collective.collective.UserAlbumsFragment;
import com.collective.collective.View.Utils.AccountDataUtils;
import com.collective.collective.View.Utils.ListsUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity
        extends AppCompatActivity
        implements AccountInfoFragment.OnFragmentInteractionListener,
        FollowingsFragment.OnListFragmentInteractionListener,
        UserAlbumsFragment.OnListFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private DrawerLayout drawerLayout;
    private String userUid;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userUid = getIntent().getStringExtra("username_uid");

        firebaseFirestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        drawerLayout = findViewById(R.id.drawer_layout);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchArtistActivity.class);
            startActivity(intent);
        });

        setNavigationViewListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_user, menu);
        MenuItem searchUserMenuItem = menu.findItem(R.id.search_friends);
        SearchView searchView = (SearchView) searchUserMenuItem.getActionView();
        Context context = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                CollectionReference userReference = firebaseFirestore.collection("users");
                userReference
                        .whereEqualTo("username", s)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String newUserUid = document.getId();
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                                    intent.putExtra("username_uid", newUserUid);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(context, "Cannot find this user. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction() {

    }

    @Override
    public void onListFragmentInteraction(Album item) {

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
        View headerView =  navigationView.getHeaderView(0);
        TextView usernameDrawer = headerView.findViewById(R.id.drawer_username);
        usernameDrawer.setText(AccountDataUtils.getAccountUsername(this));

        CircleImageView profileDrawer = headerView.findViewById(R.id.drawer_profile_picture);
        Bitmap profilePicture = AccountDataUtils.loadProfilePictureStorage(this);
        if (profilePicture != null && profileDrawer != null) {
            profileDrawer.setImageBitmap(profilePicture);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_account_info, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment currentFragment;
            switch (position) {
                case 0:
                    currentFragment = AccountInfoFragment.newInstance(userUid);
                    break;
                case 1:
                    currentFragment = UserAlbumsFragment2.newInstance(ListsUtils.ALBUM_COLLECTED_NAME);
                    break;
                case 2:
                    currentFragment = UserAlbumsFragment.newInstance(ListsUtils.ALBUM_WANTED_NAME);
                    break;
                case 3:
                    currentFragment = UserAlbumsFragment.newInstance(ListsUtils.ALBUM_LOVED_NAME);
                    break;
                case 4:
                    currentFragment = FollowingsFragment.newInstance(2);
                    break;
                default:
                    currentFragment = AccountInfoFragment.newInstance(userUid);
                    break;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return currentFragment;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }
    }
}
