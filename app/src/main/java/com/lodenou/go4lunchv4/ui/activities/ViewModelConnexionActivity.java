package com.lodenou.go4lunchv4.ui.activities;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.AuthCredential;
import com.lodenou.go4lunchv4.data.ConnexionRepository;
import com.lodenou.go4lunchv4.model.User;

public class ViewModelConnexionActivity extends ViewModel {

    private ConnexionRepository mConnexionRepository;
    MutableLiveData<User> authenticatedUserLiveData;



    public void init() {
        mConnexionRepository = new ConnexionRepository();

    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = mConnexionRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void createUser() {
         mConnexionRepository.createUserInFirestoreIfNotExists();
    }
}
