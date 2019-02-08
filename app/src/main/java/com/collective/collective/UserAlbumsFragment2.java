package com.collective.collective;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.collective.collective.Model.Firestore.Album;
import com.collective.collective.View.Adapters.CollectedAlbumsRecyclerViewAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.collective.collective.UserAlbumsFragment.OnListFragmentInteractionListener}
 * interface.
 */
public class UserAlbumsFragment2 extends Fragment {
    private static final String TYPE = "type";
    private com.collective.collective.UserAlbumsFragment.OnListFragmentInteractionListener mListener;
    private CollectedAlbumsRecyclerViewAdapter collectedAlbumsRecyclerViewAdapter;

    public static final int USER_ALBUM_LIST_TYPE_COLLECTED = 1;
    public static final int USER_ALBUM_LIST_TYPE_WANTED = 2;
    public static final int USER_ALBUM_LIST_TYPE_LOVED = 3;
    String listType;


    String ownerUid = FirebaseAuth.getInstance().getUid();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference albumsReference = firebaseFirestore
            .collection("users")
            .document(ownerUid)
            .collection("collected");

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserAlbumsFragment2() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserAlbumsFragment2 newInstance(String type) {
        UserAlbumsFragment2 fragment = new UserAlbumsFragment2();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listType = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);

        setUpRecyclerView(view, listType);
        return view;
    }

    private void setUpRecyclerView(View view, String listType) {
        Query query = albumsReference;
        FirestoreRecyclerOptions<Album> albumFirestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Album>()
                        .setQuery(query, Album.class)
                        .build();
        collectedAlbumsRecyclerViewAdapter = new CollectedAlbumsRecyclerViewAdapter(albumFirestoreRecyclerOptions);

        RecyclerView recyclerView = view.findViewById(R.id.list_albums);
        recyclerView.setLayoutManager(new RecyclerView.LayoutManager() {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return null;
            }
        });
        recyclerView.setAdapter(collectedAlbumsRecyclerViewAdapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof com.collective.collective.UserAlbumsFragment.OnListFragmentInteractionListener) {
            mListener = (com.collective.collective.UserAlbumsFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Album item);
    }

    @Override
    public void onStart() {
        super.onStart();
        collectedAlbumsRecyclerViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        collectedAlbumsRecyclerViewAdapter.stopListening();
    }
}

