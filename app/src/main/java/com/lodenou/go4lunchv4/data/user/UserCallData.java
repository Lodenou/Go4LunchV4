package com.lodenou.go4lunchv4.data.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.model.User;
import com.google.firebase.firestore.Query;



public class UserCallData {
    private static final String COLLECTION_NAME = "users";
    private final FirebaseFirestore firebaseFirestore;

    public UserCallData(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    public CollectionReference getUsersCollection() {
        return firebaseFirestore.collection(COLLECTION_NAME);
    }

    public Task<Void> createUser(String uid, String userName, String userAvatarUrl, String userEmail,
                                 String favoriteRestaurantId, String restaurantChosenId, String restaurantChosenName) {
        User userToCreate = new User(uid, userName, userAvatarUrl, userEmail, favoriteRestaurantId,
                restaurantChosenId, restaurantChosenName);
        return getUsersCollection().document(uid).set(userToCreate);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return getUsersCollection().document(uid).get();
    }

    public Query getAllUsers() {
        return getUsersCollection();
    }
}
