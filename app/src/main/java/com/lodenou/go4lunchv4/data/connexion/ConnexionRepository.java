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

public class ConnexionRepository implements IConnexionRepository {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    User mUser;

    private UserCallData userCallData;

    // Constructor for injection
    public ConnexionRepository(UserCallData userCallData) {
        this.userCallData = userCallData;
    }

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
            } else {
                Log.d("123", "OnFailure: Login failed" + Objects.requireNonNull(authTask.getException()).getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

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
                            Log.d("TAG", "onFailure: firestore error 1 ");
                        });
                    }
                }
            });
        }
    }
}
