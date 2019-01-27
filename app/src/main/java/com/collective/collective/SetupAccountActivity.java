package com.collective.collective;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.collective.collective.View.Activities.AccountActivity;
import com.collective.collective.View.Fragments.AddAvatarBottomSheetDialogFragment;
import com.collective.collective.View.Utils.OnCropProfilePicture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupAccountActivity extends AppCompatActivity implements OnCropProfilePicture {

    private static final String SETUP_LOGGING_TAG = "SetupAccountActivity";
    private StorageReference storageRef;
    private String firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public static final int GALLERY_PICK = 1;
    public static final int CAMERA_PICK = 2;
    private static final int MY_CAMERA_PERMISSION_CODE = 3;

    private Bitmap profilePicture;
    private Uri croppedAvatarUri;

    @BindView(R.id.username_edittext)
    EditText usernameEditText;
    @BindView(R.id.description_edittext)
    EditText descriptionEditText;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.edit_avatar_button)
    Button editAvatarButton;

    @OnClick(R.id.edit_avatar_button)
    void editAvatar() {
        AddAvatarBottomSheetDialogFragment addAvatarBottomDialogFragment =
                AddAvatarBottomSheetDialogFragment.newInstance();
        addAvatarBottomDialogFragment.show(getSupportFragmentManager(),
                "add_avatar_dialog_fragment");
        addAvatarBottomDialogFragment.addListener(this);
    }

    @OnClick(R.id.save_account_details_button)
    void setupAccount() {
        String username = usernameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter the username.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please provide some description.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("description", description);

        CollectionReference userReference = firebaseFirestore.collection("users");
        userReference.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        firebaseFirestore.collection("users").document(firebaseAuth)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> Log.d(SETUP_LOGGING_TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(SETUP_LOGGING_TAG, "Error writing document", e));

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "This username is already taken. Please, choose another one.",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        if (croppedAvatarUri != null) {
            StorageReference storageReference = storageRef.child("images/users/" + username + ".jpg");
            storageReference.putFile(croppedAvatarUri)
                    .addOnSuccessListener(taskSnapshot -> onUserAccountSetupFinished())
                    .addOnFailureListener(e ->
                            Toast.makeText(getApplicationContext(),
                                    "Cannot upload profile picture. Please try again later",
                                    Toast.LENGTH_SHORT).show())
            ;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        ButterKnife.bind(this);

        if (profilePicture != null) {
            avatar.setImageBitmap(profilePicture);
        }

        firebaseAuth = FirebaseAuth.getInstance().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GALLERY_PICK || requestCode == CAMERA_PICK) && resultCode == RESULT_OK && data != null) {
            Uri avatarUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setRequestedSize(300, 300)
                    .setActivityTitle("Customize your profile picture")
                    .setBackgroundColor(R.color.white)
                    .setSnapRadius(1)
                    .setBorderCornerColor(R.color.colorAccent)
                    .setGuidelinesColor(R.color.black)
                    .setBorderLineColor(R.color.colorAccent)
                    .setActivityMenuIconColor(R.color.black)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            croppedAvatarUri = result.getUri();
            try {
                profilePicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedAvatarUri);
                avatar.setImageBitmap(profilePicture);
            } catch (Exception exception) {
                Log.e(SetupAccountActivity.class.getName(), "Error saving bitmap", exception);
            }
        }
    }

    private void onUserAccountSetupFinished() {
        finish();
        Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
        intent.putExtra("username_uid", FirebaseAuth.getInstance().getUid());
        startActivity(intent);
    }

    @Override
    public void onCropProfilePicture(int type) {
        switch (type) {
            case 1:
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
                break;
            case 2:
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PICK);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PICK);
            } else {
                Toast.makeText(this, "Camera permission denied. You cannot add profile picture.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
