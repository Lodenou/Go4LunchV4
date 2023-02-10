package com.lodenou.go4lunchv4.data.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lodenou.go4lunchv4.model.User;
import com.google.firebase.firestore.Query;


public class UserCallData {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String uid, String userName, String userAvatarUrl,String userEmail
            , String FavoriteRestaurantId, String restaurantChosenId, String restaurantChosenName) {
        User userToCreate = new User(uid, userName,  userAvatarUrl, userEmail, FavoriteRestaurantId,
                restaurantChosenId,restaurantChosenName );
        return UserCallData.getUsersCollection().document(uid).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserCallData.getUsersCollection().document(uid).get();
    }

    public static Query getAllUsers() {
        return UserCallData.getUsersCollection();
    }
}
