package com.lodenou.go4lunchv4.data.connexion;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv4.model.User;

public interface IConnexionRepository {
    MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential);

    FirebaseUser getCurrentUser();

    void createUserInFirestoreIfNotExists();


}
