package com.lodenou.go4lunchv4.data;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.model.User;

import java.util.Objects;

public class ConnexionRepository {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//    private CollectionReference usersRef = rootRef.collection("users");
    User mUser;

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
                Log.d("123", "OnFailure: Loggin failed" + Objects.requireNonNull(authTask.getException()).getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    //    public MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
//        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
//        DocumentReference uidRef = usersRef.document(authenticatedUser.getUid());
//        uidRef.get().addOnCompleteListener(uidTask -> {
//            if (uidTask.isSuccessful()) {
//                DocumentSnapshot document = uidTask.getResult();
//                if (!document.exists()) {
//                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
//                        if (userCreationTask.isSuccessful()) {
//                            authenticatedUser.isCreated = true;
//                            newUserMutableLiveData.setValue(authenticatedUser);
//                        } else {
//                            Log.d("123", "OnFailure: Loggin failed" + (userCreationTask.getException().getMessage()));
//                        }
//                    });
//                } else {
//                    newUserMutableLiveData.setValue(authenticatedUser);
//                }
//            } else {
//                Log.d("123", "OnFailure: Loggin failed" + (uidTask.getException().getMessage()));
//            }
//        });
//        return newUserMutableLiveData;
//    }
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

            UserCallData.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mUser = documentSnapshot.toObject(User.class);
                    if (mUser == null) {
                        UserCallData.createUser(uid, username, urlPicture, email, " ", "", "").addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Firestore Error 1", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "onFailure: firestore error 1 ");
                        });
                    }
                }
            });
        }
    }
}
