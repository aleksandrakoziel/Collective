package com.collective.collective;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.collective.collective.View.Activities.AccountActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String REGISTRATION_TAG = "REGISTRATION";

    private GoogleSignInClient googleSignInClient;

    @BindView(R.id.login)
    EditText loginEditText;
    @BindView(R.id.password)
    EditText passwordEditText;

    @OnClick(R.id.google_sign_button)
    void registerWithGoogle() {
        googleSignIn();
    }

    @OnClick(R.id.register_button)
    void registerWithPassword() {
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(login)) {
            Toast.makeText(this, "Enter your email address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(login, password);
        }
    }

    @OnClick(R.id.registered_info)
    void goToSignIn() {
        startActivity(new Intent(getApplicationContext(), SignActivity.class));
    }

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (firebaseAuth.getCurrentUser() != null) {
            onUserSignedIn();
        }
    }

    private void registerUser(@NonNull String login, @NonNull String password) {
        firebaseAuth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "You were successfully registered in Collective", Toast.LENGTH_SHORT).show();
                onNewUserSignedIn();
            } else {
                Exception e = task.getException();
                if (e != null) {
                    String failMessage = e.getMessage();
                    Log.e("CREATEACC", "Something went wrong: " + failMessage, e);
                    Toast.makeText(RegisterActivity.this, "Could not registerWithPassword. " + failMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(REGISTRATION_TAG, "Google signWithPassword in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(REGISTRATION_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(REGISTRATION_TAG, "signInWithCredential: success");
                        onNewUserSignedIn();
                    } else {
                        // If signWithPassword in fails, display a message to the user.
                        Log.w(REGISTRATION_TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.register_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT);
                    }
                });
    }

    private void onUserSignedIn() {
        finish();
        Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
        intent.putExtra("username_uid", firebaseAuth.getUid());
        startActivity(intent);
    }

    private void onNewUserSignedIn() {
        finish();
        startActivity(new Intent(getApplicationContext(), SetupAccountActivity.class));
    }
}
