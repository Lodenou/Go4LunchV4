package com.lodenou.go4lunchv4.ui.activities.connexion;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.data.connexion.ConnexionRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.User;

public class ViewModelConnexionActivity extends ViewModel {

    private ConnexionRepository mConnexionRepository;
    MutableLiveData<User> authenticatedUserLiveData;

    public void init() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserCallData userCallData = new UserCallData(firebaseFirestore);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mConnexionRepository = new ConnexionRepository(userCallData, firebaseAuth);
    }
    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = mConnexionRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void createUser() {
         mConnexionRepository.createUserInFirestoreIfNotExists();
    }
}
