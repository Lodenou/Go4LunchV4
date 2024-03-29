package com.lodenou.go4lunchv4.ui.activities.connexion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.databinding.ActivityConnexionBinding;
import com.lodenou.go4lunchv4.ui.activities.main.MainActivity;

/**
 * ConnexionActivity handles user authentication, including Google Sign-In.
 */

public class ConnexionActivity extends AppCompatActivity {

    private ActivityConnexionBinding mBinding;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private final String TAG = "GOOGLE_SIGN_IN_TAG";
    private final int RC_SIGN_IN = 123;
    private ViewModelConnexionActivity mViewModelConnexionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityConnexionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        initGoogleSignInButton();
        initViewModel();
        initGoogleSignInClient();
    }

    /**
     * Initialize the Google Sign-In button click listener.
     */
    private void initGoogleSignInButton() {
        mBinding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    /**
     * Initialize the ViewModel for managing authentication and user data.
     */
    private void initViewModel() {
        mViewModelConnexionActivity = new ViewModelProvider(this).get(ViewModelConnexionActivity.class);
        mViewModelConnexionActivity.init();
    }

    /**
     * Initialize the Google Sign-In client with appropriate options.
     */
    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    /**
     * Start the Google Sign-In flow.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieve the Google Auth Credential from the Google Sign-In account.
     *
     * @param googleSignInAccount The Google Sign-In account.
     */
    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    /**
     * Sign in to Firebase with the Google Auth Credential.
     *
     * @param googleAuthCredential The Google Auth Credential.
     */
    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        mViewModelConnexionActivity.signInWithGoogle(googleAuthCredential);
        mViewModelConnexionActivity.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            mViewModelConnexionActivity.createUser();
            goToMainActivity();
        });
    }

    /**
     * Navigate to the main activity upon successful authentication.
     */
    private void goToMainActivity() {
        Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}












