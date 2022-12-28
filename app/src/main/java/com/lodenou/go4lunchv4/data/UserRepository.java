package com.lodenou.go4lunchv4.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lodenou.go4lunchv4.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {

    private static UserRepository instance;
    private ArrayList<User> datasetUsers = new ArrayList<>();
    MutableLiveData<List<User>> dataUsers = new MutableLiveData<>();
    private ArrayList<String> datasetChosenRestaurantId = new ArrayList<>();
    MutableLiveData<List<String>> dataChosenRestaurantId = new MutableLiveData<>();
    MutableLiveData<User> dataUser = new MutableLiveData<>();
    User mUser;


    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }



    public MutableLiveData<List<User>> getUsers(){

        UserCallData.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    datasetUsers.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("123", document.getId() + " => " + document.getData());
                        mUser = document.toObject(User.class);
                        datasetUsers.add(mUser);
                    }
                    dataUsers.setValue(datasetUsers);
                } else {
                    Log.d("123", "Error getting documents: ", task.getException());
                }
            }
        });
         return dataUsers;
    }

    public  MutableLiveData<User> getUser(){
        String userId = Objects.requireNonNull(getCurrentUser()).getUid();
        UserCallData.getUser(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    mUser = document.toObject(User.class);
                    dataUser.setValue(mUser);
                }
            }
        });

        return dataUser;
    }

    public MutableLiveData<List<String>> getRestaurantChosenId(){
        UserCallData.getAllUsers().whereNotEqualTo("restaurantChosenId", "").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    datasetChosenRestaurantId.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("123", document.getId() + " => " + document.getData());
                        mUser = document.toObject(User.class);
                        datasetChosenRestaurantId.add(mUser.getRestaurantChosenId());
                    }
                    dataChosenRestaurantId.setValue(datasetChosenRestaurantId);
                }
            }
        });
        return dataChosenRestaurantId;
    }



    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }





}
