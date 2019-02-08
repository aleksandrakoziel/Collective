package com.collective.collective;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.collective.collective.Model.Firestore.User;
import com.collective.collective.View.Utils.AccountDataUtils;
import com.collective.collective.ViewModel.UserViewModel;
import com.collective.collective.ViewModel.UserViewModelFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInfoFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    boolean followed;

    @BindView(R.id.profile_picture)
    CircleImageView profilePicture;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.follow_button)
    Button followButton;

    @OnClick(R.id.follow_button)
    void followUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", currentUsername);

        String mainUserUid = FirebaseAuth.getInstance().getUid();

        if (mainUserUid != null) {
            if (followed) {
                firebaseFirestore.collection("user")
                        .document(mainUserUid)
                        .collection("followings")
                        .document(ownerUid)
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                                "You successfully unfollowed " + currentUsername + ".",
                                Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(),
                                    "You cannot unfollow this user. Please try again later. ",
                                    Toast.LENGTH_SHORT).show();
                            Log.w("ACCOUNT", "Error deleting document", e);
                        });
            } else {
                firebaseFirestore.collection("users")
                        .document(ownerUid)
                        .collection("followings")
                        .document(currentUsername)
                        .set(userData)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                                "You are now following " + currentUsername,
                                Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Log.e("ACCOUNT", "error saving friend", e);
                            Toast.makeText(getContext(),
                                    "You cannot follow " + currentUsername + " user. Please try again later.",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
            checkIfFollowed();
        }
    }

    private User user;
    private String currentUsername;
    private String ownerUid = FirebaseAuth.getInstance().getUid();
    @Inject
    UserViewModelFactory userViewModelFactory;

    private String usernameUid;

    private OnFragmentInteractionListener mListener;

    public AccountInfoFragment() {
        // Required empty public constructor
    }

    public static AccountInfoFragment newInstance(String usernameUid) {
        AccountInfoFragment fragment = new AccountInfoFragment();
        Bundle args = new Bundle();
        args.putString("username_uid", usernameUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.usernameUid = getArguments().getString("username_uid");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View accountView = inflater.inflate(R.layout.fragment_account_info, container, false);
        ButterKnife.bind(this, accountView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ownerUid = firebaseAuth.getUid();

        if (isMainAccount()) {
            followButton.setVisibility(View.INVISIBLE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            checkIfFollowed();
        }

        if (followed) {
            followButton.setText("Followed");
        } else {
            followButton.setText("Follow");
        }

        userViewModelFactory = new UserViewModelFactory(usernameUid);
        UserViewModel userViewModel =
                ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel.class);
        LiveData<User> userLiveData = userViewModel.getUserLiveData();

        userLiveData.observe(this, newUser -> {
            if (newUser != null) {
                user = newUser;
                currentUsername = user.getUsername();
                String savedUsername = AccountDataUtils.getAccountUsername(getContext());
                if (Objects.equals(savedUsername, AccountDataUtils.DEFAULT_USERNAME) && isMainAccount()) {
                    AccountDataUtils.saveAccountUsername(getContext(), currentUsername);
                }
                username.setText(user.getUsername());
                description.setText(user.getDescription());
                loadProfilePicture(currentUsername);
            } else if (isMainAccount()) {
                startActivity(new Intent(getContext(), SetupAccountActivity.class));
            }
        });
        return accountView;
    }

    private boolean isMainAccount() {
        return usernameUid.equals(FirebaseAuth.getInstance().getUid());
    }

    void loadProfilePicture(String username) {
        Bitmap profile = AccountDataUtils.loadProfilePictureStorage(getContext());
        if (profile != null && isMainAccount()) {
            profilePicture.setImageBitmap(profile);
        } else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("images/users/" + username + ".jpg");
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> loadProfilePictureIntoImageView(storageReference, true))
                    .addOnFailureListener(e -> {
                        Log.e("ACCOUNT", "Cannot load image with error: ", e);
                        loadProfilePictureIntoImageView(storageReference, false);
                    });
        }
    }

    void loadProfilePictureIntoImageView(StorageReference storageReference, boolean isResourceExist) {
        if (isResourceExist) {
            GlideApp.with(this)
                    .load(storageReference)
                    .into(profilePicture);
            if (isMainAccount()) {
                try {
                    final File localFile = File.createTempFile("Images", "bmp");
                    storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        AccountDataUtils.saveProfilePicture(bitmap, getContext());
                    }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            profilePicture.setImageResource(R.drawable.cd);
        }
    }

    private void checkIfFollowed() {
        CollectionReference userReference = firebaseFirestore
                .collection("users")
                .document(ownerUid)
                .collection("followings");
        userReference.whereEqualTo("username", currentUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        followed = false;
                        followButton.setText("Follow");
                    } else {
                        followed = true;
                        followButton.setText("Followed");
                        followButton.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                    }
                });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
