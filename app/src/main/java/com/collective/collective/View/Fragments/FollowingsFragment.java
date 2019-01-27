package com.collective.collective.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.collective.collective.Model.Firestore.Following;
import com.collective.collective.R;
import com.collective.collective.View.Adapters.CollectedAlbumsRecyclerViewAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FollowingsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    private String ownerUid = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference followingsReference = firebaseFirestore
            .collection("users")
            .document(ownerUid)
            .collection("followings");

    private CollectedAlbumsRecyclerViewAdapter.FollowingsRecyclerViewAdapter followingsRecyclerViewAdapter;

    public static final int USER_LIST_FOLLOWERS = 1;
    public static final int USER_LIST_FOLLOWINGS = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FollowingsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FollowingsFragment newInstance(int columnCount) {
        FollowingsFragment fragment = new FollowingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private void setUpRecyclerView(View view) {
        Query query = followingsReference.orderBy("username", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Following> followingFirestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Following>()
                .setQuery(query, Following.class)
                .build();

        followingsRecyclerViewAdapter = new CollectedAlbumsRecyclerViewAdapter.FollowingsRecyclerViewAdapter(followingFirestoreRecyclerOptions);

        RecyclerView recyclerView = view.findViewById(R.id.followings_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        recyclerView.setAdapter(followingsRecyclerViewAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        setUpRecyclerView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        followingsRecyclerViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        followingsRecyclerViewAdapter.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
        void onListFragmentInteraction();
    }
}
