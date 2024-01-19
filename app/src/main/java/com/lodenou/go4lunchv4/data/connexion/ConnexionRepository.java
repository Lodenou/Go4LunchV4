package com.lodenou.go4lunchv4.data.connexion;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.User;

import java.util.Objects;

/**
 * This class implements the ConnexionRepository interface and provides methods for user authentication and user data management.
 */
public class ConnexionRepository implements IConnexionRepository {

    User mUser;

    private FirebaseAuth firebaseAuth;
    private UserCallData userCallData;

    /**
     * Constructs a ConnexionRepository instance.
     *
     * @param userCallData    The UserCallData instance for user-related operations.
     * @param firebaseAuth    The FirebaseAuth instance for authentication.
     */
    public ConnexionRepository(UserCallData userCallData, FirebaseAuth firebaseAuth) {
        this.userCallData = userCallData;
        this.firebaseAuth = firebaseAuth;
    }

    /**
     * Signs in a user with Google credentials.
     *
     * @param googleAuthCredential The Google authentication credential.
     * @return A MutableLiveData containing the authenticated user.
     */
    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String avatar = firebaseUser.getPhotoUrl().toString();
                    String email = firebaseUser.getEmail();
                    User user = new User(uid, name, avatar, email, "", "", "");
                    authenticatedUserMutableLiveData.setValue(user);
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }

    /**
     * Retrieves the currently authenticated Firebase user.
     *
     * @return The currently authenticated Firebase user or null if not authenticated.
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Creates a user in Firestore if the user does not exist.
     */
    public void createUserInFirestoreIfNotExists() {
        if (this.getCurrentUser() != null) {
            final String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            final String username = this.getCurrentUser().getDisplayName();
            final String uid = this.getCurrentUser().getUid();
            final String email = this.getCurrentUser().getEmail();
            userCallData.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mUser = documentSnapshot.toObject(User.class);
                    if (mUser == null) {
                        userCallData.createUser(uid, username, urlPicture, email, " ", "", "").addOnFailureListener(e -> {
                        });
                    }
                }
            });
        }
    }
}
